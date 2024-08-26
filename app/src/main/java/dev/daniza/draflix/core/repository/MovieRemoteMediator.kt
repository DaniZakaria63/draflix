package dev.daniza.draflix.core.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dev.daniza.draflix.local.dao.RemoteKeysDao
import dev.daniza.draflix.local.dao.SearchDao
import dev.daniza.draflix.local.entity.RemoteKeysEntity
import dev.daniza.draflix.local.entity.SearchEntity
import dev.daniza.draflix.network.OMDBService
import dev.daniza.draflix.network.model.ResponseSearchList
import dev.daniza.draflix.network.model.responseParsing
import dev.daniza.draflix.utilities.DEFAULT_QUERY_TITLE
import dev.daniza.draflix.utilities.DEFAULT_QUERY_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val OMDBService: OMDBService,
    private val remoteKeysDao: RemoteKeysDao,
    private val searchDao: SearchDao,
    private val query: Pair<String, String>? = Pair(DEFAULT_QUERY_TYPE, DEFAULT_QUERY_TITLE),
) : RemoteMediator<Int, SearchEntity>() {
    private val STARTING_PAGE_INDEX: Int = 1

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH

        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
        return if (System.currentTimeMillis() - (remoteKeysDao.getCreationTime()
                ?: 0) < cacheTimeout
        ) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SearchEntity>
    ): MediatorResult {
        val page: Int = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        try {
            val response = withContext(Dispatchers.IO) {
                OMDBService.getMovies(
                    type = query?.first.orEmpty(),
                    search = query?.second.orEmpty(),
                    page = page,
                )
            }
            val result = responseParsing(response, ResponseSearchList::class.java)
            if (result.isSuccess) {
                val movies = result.getOrNull()?.Search.orEmpty()
                if (loadType == LoadType.REFRESH) {
                    withContext(Dispatchers.IO) {
                        remoteKeysDao.clearRemoteKeys()
                        searchDao.clearSearch()
                    }
                }
                val prefKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (movies.isEmpty()) null else page + 1
                val remoteKeys = movies.map {
                    RemoteKeysEntity(
                        movieId = it.imdbID?.ifEmpty { "-" }.toString(),
                        prevKey = prefKey,
                        currentPage = page,
                        nextKey = nextKey
                    )
                }
                withContext(Dispatchers.IO) {
                    remoteKeysDao.insertAll(remoteKeys)
                    searchDao.insertAll(movies.map {
                        SearchEntity(
                            id = it.imdbID?.ifEmpty { "-" }.toString(),
                            title = it.Title?.ifEmpty { "-" }.toString(),
                            year = it.Year?.ifEmpty { "-" }.toString(),
                            type = it.Type?.ifEmpty { "-" }.toString(),
                            poster = it.Poster.orEmpty()
                        )
                    })
                }
                return MediatorResult.Success(endOfPaginationReached = movies.isEmpty())
            } else {
                return MediatorResult.Error(
                    result.exceptionOrNull() ?: Exception("Unknown Error")
                )
            }
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, SearchEntity>
    ): RemoteKeysEntity? {
        return withContext(Dispatchers.IO) {
            state.anchorPosition?.let { position ->
                state.closestItemToPosition(position)?.id?.let { id ->
                    remoteKeysDao.getRemoteKeyByMovieId(id)
                }
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, SearchEntity>
    ): RemoteKeysEntity? {
        return withContext(Dispatchers.IO) {
            state.pages.firstOrNull {
                it.data.isNotEmpty()
            }?.data?.firstOrNull()?.let { movie ->
                remoteKeysDao.getRemoteKeyByMovieId(movie.id)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, SearchEntity>
    ): RemoteKeysEntity? {
        return withContext(Dispatchers.IO) {
            state.pages.lastOrNull {
                it.data.isNotEmpty()
            }?.data?.lastOrNull()?.let { movie ->
                remoteKeysDao.getRemoteKeyByMovieId(movie.id)
            }
        }
    }
}