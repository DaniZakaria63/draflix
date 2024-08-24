package dev.daniza.draflix.core.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.daniza.draflix.local.dao.RemoteKeysDao
import dev.daniza.draflix.local.dao.SearchDao
import dev.daniza.draflix.network.OMDBService
import dev.daniza.draflix.network.model.ResponseSearchListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface MovieRepository {
    suspend fun getMovies(query: String): Flow<PagingData<ResponseSearchListItem>>
}

@OptIn(ExperimentalPagingApi::class)
class MovieRepositoryImpl @Inject constructor(
    private val OMDBService: OMDBService,
    private val remoteKeysDao: RemoteKeysDao,
    private val searchDao: SearchDao,
) : MovieRepository {
    override suspend fun getMovies(query: String): Flow<PagingData<ResponseSearchListItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                searchDao.getAll()
            },
            remoteMediator = MovieRemoteMediator(
                OMDBService,
                remoteKeysDao,
                searchDao
            )
        ).flow.map { pagingdata ->
            pagingdata.map {
                ResponseSearchListItem(
                    Title = it.title,
                    Year = it.year,
                    imdbID = it.id,
                    Poster = it.poster
                )
            }
        }
    }
}