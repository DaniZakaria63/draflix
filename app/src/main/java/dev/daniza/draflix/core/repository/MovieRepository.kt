package dev.daniza.draflix.core.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.daniza.draflix.local.dao.MovieDao
import dev.daniza.draflix.local.dao.RemoteKeysDao
import dev.daniza.draflix.local.dao.SearchDao
import dev.daniza.draflix.local.entity.MovieEntity
import dev.daniza.draflix.network.OMDBService
import dev.daniza.draflix.network.model.ResponseSearchListItem
import dev.daniza.draflix.network.model.ResponseSingle
import dev.daniza.draflix.network.model.responseParsing
import dev.daniza.draflix.utilities.DEFAULT_QUERY_TITLE
import dev.daniza.draflix.utilities.DEFAULT_QUERY_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface MovieRepository {
    fun getMovies(type: String, title: String): Flow<PagingData<ResponseSearchListItem>>
    suspend fun getMovieDetail(id: String): Result<ResponseSingle>
}

@OptIn(ExperimentalPagingApi::class)
class MovieRepositoryImpl @Inject constructor(
    private val OMDBService: OMDBService,
    private val remoteKeysDao: RemoteKeysDao,
    private val searchDao: SearchDao,
    private val movieDao: MovieDao
) : MovieRepository {
    override fun getMovies(type: String, title: String): Flow<PagingData<ResponseSearchListItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                searchDao.getSearchByQuery(
                    type = DEFAULT_QUERY_TYPE,
                    title = DEFAULT_QUERY_TITLE
                )
            },
            remoteMediator = MovieRemoteMediator(
                OMDBService,
                remoteKeysDao,
                searchDao,
                Pair(type, title)
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

    override suspend fun getMovieDetail(id: String): Result<ResponseSingle> {
        // TODO: CHECK FOR CONNECTION FIRST
        return try {
            val response = withContext(Dispatchers.IO) {
                OMDBService.getMovies(id = id)
            }
            val result = responseParsing(response, ResponseSingle::class.java)
            if (response.isSuccessful) {
                val movie = result.getOrNull() ?: ResponseSingle()
                movieDao.insertMovie(
                    MovieEntity(
                        id = movie.imdbID.orEmpty(),
                        title = movie.Title.orEmpty(),
                        year = movie.Year.orEmpty(),
                        released = movie.Released.orEmpty(),
                        runtime = movie.Runtime.orEmpty(),
                        genre = movie.Genre.orEmpty(),
                        poster = movie.Poster.orEmpty(),
                        director = movie.Director.orEmpty(),
                        writer = movie.Writer.orEmpty(),
                        actor = movie.Actors.orEmpty(),
                        plot = movie.Plot.orEmpty(),
                        rating = movie.imdbRating.orEmpty(),
                        createdDate = System.currentTimeMillis()
                    )
                )
                Result.success(movie)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown Error"))
            }
        } catch (e: IOException) {
            return Result.failure(e)
        } catch (e: HttpException) {
            return Result.failure(e)
        }
    }
}