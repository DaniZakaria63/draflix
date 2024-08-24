package dev.daniza.draflix.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.gson.Gson
import dev.daniza.draflix.local.dao.MovieDao
import dev.daniza.draflix.local.dao.SearchDao
import dev.daniza.draflix.local.entity.MovieEntity
import dev.daniza.draflix.local.entity.SearchEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
@SmallTest
class DraflixDatabaseTest {
    private lateinit var database: DraflixDatabase
    private lateinit var movieDao: MovieDao
    private lateinit var searchDao: SearchDao

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DraflixDatabase::class.java
        ).allowMainThreadQueries().build()
        movieDao = database.movieDao()
        searchDao = database.searchDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun whenInsertMovie_thenGetMovieById() = runBlocking {
        val movie = MovieEntity(
            id = "tt3896198",
            title = "Guardians of the Galaxy Vol. 2",
            year = "2017",
            released = "05 May 2017",
            runtime = "136 min",
            genre = "Action, Adventure, Comedy",
            director = "James Gunn",
            writer = "James Gunn, Dan Abnett, Andy Lanning",
            actor = "Chris Pratt, Zoe Saldana, Dave Bautista",
            plot = "The Guardians struggle to keep together as a team while dealing with their personal family issues, notably Star-Lord's encounter with his father, the ambitious celestial being Ego.",
            rating = "7.6",
            createdDate = System.currentTimeMillis()
        )
        movieDao.insertMovie(movie)

        val result = movieDao.getMovieById(movie.id)
        assertThat(result, `is`(movie))
    }

    @Test
    fun givenInsertSearch_whenGetAll_thenGetSearchList() = runBlocking {
        val searchList = Gson().fromJson(dummySearchList, Array<SearchEntity>::class.java)
        searchDao.insertAll(searchList.toList())

        val result = searchDao.getAll()
        assertThat(result.size, `is`(10))
    }

    @Test
    fun givenInsertSearch_whenGetSearchByQuery_thenGetSearchList() = runBlocking {
        val searchList = Gson().fromJson(dummySearchList, Array<SearchEntity>::class.java)
        searchDao.insertAll(searchList.toList())

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            searchDao.getSearchByQuery(type = "series").collect { result ->
                assertThat(result.size, `is`(3))
                latch.countDown()
            }
        }
        latch.await()
        job.cancelAndJoin()
    }
}

const val dummySearchList = """
  [
    {
      "title": "The Avengers",
      "year": "2012",
      "id": "tt0848228",
      "type": "movie",
      "poster": "https://m.media-amazon.com/images/M/MV5BNDYxNjQyMjAtNTdiOS00NGYwLWFmNTAtNThmYjU5ZGI2YTI1XkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg"
    },
    {
      "title": "Avengers: Endgame",
      "year": "2019",
      "id": "tt4154796",
      "type": "movie",
      "poster": "https://m.media-amazon.com/images/M/MV5BMTc5MDE2ODcwNV5BMl5BanBnXkFtZTgwMzI2NzQ2NzM@._V1_SX300.jpg"
    },
    {
      "title": "Avengers: Infinity War",
      "year": "2018",
      "id": "tt4154756",
      "type": "movie",
      "poster": "https://m.media-amazon.com/images/M/MV5BMjMxNjY2MDU1OV5BMl5BanBnXkFtZTgwNzY1MTUwNTM@._V1_SX300.jpg"
    },
    {
      "title": "Avengers: Age of Ultron",
      "year": "2015",
      "id": "tt2395427",
      "type": "movie",
      "poster": "https://m.media-amazon.com/images/M/MV5BMTM4OGJmNWMtOTM4Ni00NTE3LTg3MDItZmQxYjc4N2JhNmUxXkEyXkFqcGdeQXVyNTgzMDMzMTg@._V1_SX300.jpg"
    },
    {
      "title": "The Avengers",
      "year": "1998",
      "id": "tt0118661",
      "type": "movie",
      "poster": "https://m.media-amazon.com/images/M/MV5BZTQ4NmIzMTktOTdjOC00NzE4LWIzNTgtODkwNzM5ZjUzZDkxXkEyXkFqcGdeQXVyMTUzMDUzNTI3._V1_SX300.jpg"
    },
    {
      "title": "The Avengers: Earth's Mightiest Heroes",
      "year": "2010–2012",
      "id": "tt1626038",
      "type": "series",
      "poster": "https://m.media-amazon.com/images/M/MV5BYzA4ZjVhYzctZmI0NC00ZmIxLWFmYTgtOGIxMDYxODhmMGQ2XkEyXkFqcGdeQXVyNjExODE1MDc@._V1_SX300.jpg"
    },
    {
      "title": "Ultimate Avengers: The Movie",
      "year": "2006",
      "id": "tt0491703",
      "type": "movie",
      "poster": "https://m.media-amazon.com/images/M/MV5BMTYyMjk0NTMwMl5BMl5BanBnXkFtZTgwNzY0NjAwNzE@._V1_SX300.jpg"
    },
    {
      "title": "The Avengers",
      "year": "1961–1969",
      "id": "tt0054518",
      "type": "series",
      "poster": "https://m.media-amazon.com/images/M/MV5BZWI4ZWM4ZWQtODk1ZC00MzMxLThlZmMtOGFmMTYxZTAwYjc5XkEyXkFqcGdeQXVyMTk0MjQ3Nzk@._V1_SX300.jpg"
    },
    {
      "title": "Ultimate Avengers II",
      "year": "2006",
      "id": "tt0803093",
      "type": "movie",
      "poster": "https://m.media-amazon.com/images/M/MV5BZjI3MTI5ZTYtZmNmNy00OGZmLTlhNWMtNjZiYmYzNDhlOGRkL2ltYWdlL2ltYWdlXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_SX300.jpg"
    },
    {
      "title": "Avengers Assemble",
      "year": "2012–2019",
      "id": "tt2455546",
      "type": "series",
      "poster": "https://m.media-amazon.com/images/M/MV5BMTY0NTUyMDQwOV5BMl5BanBnXkFtZTgwNjAwMTA0MDE@._V1_SX300.jpg"
    }
  ]
"""