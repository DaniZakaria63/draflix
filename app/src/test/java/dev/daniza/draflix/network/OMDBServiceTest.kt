package dev.daniza.draflix.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import dev.daniza.draflix.network.model.ResponseSearchList
import dev.daniza.draflix.network.model.ResponseSingle
import dev.daniza.draflix.network.model.responseParsing
import dev.daniza.draflix.utilities.OMDB_BASE_URL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.SocketException
import java.net.SocketTimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class OMDBServiceTest {
    private lateinit var service: OMDBService
    private val realKeyAccess = "10a5e46b"
    private val fakeKeyAccess = "fake_key_access"
    private var omdbKeyInterceptor = OMDBKeyInterceptor(keyAccess = realKeyAccess)
    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        service = OMDBService.create(OMDB_BASE_URL, omdbKeyInterceptor)
        mockWebServer.start(port = 8000)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun should_get_success_response() = runTest {
        val response = service.getMovies(id = "tt3896198")
        advanceUntilIdle()
        assertTrue(response.isSuccessful)
        assertThat(response.code(), `is`(200))
    }

    @Test
    fun should_get_error_response() = runTest {
        omdbKeyInterceptor = OMDBKeyInterceptor(keyAccess = fakeKeyAccess)
        service = OMDBService.create(OMDB_BASE_URL, omdbKeyInterceptor)
        val response = service.getMovies(id = "tt3896198")
        advanceUntilIdle()
        assertFalse(response.isSuccessful)
        assertThat(response.code(), `is`(401))
    }

    @Test
    fun given_rightId_when_getMovies_then_returnResponseSingle() = runTest {
        val response = service.getMovies(id = "tt3896198")
        advanceUntilIdle()
        assertTrue(response.isSuccessful)
        assertThat(response.code(), `is`(200))

        // Validate the Response is True or False
        val result = response.body()?.get("Response")!!
        assertThat(result.asString, `is`("True"))
    }

    @Test
    fun given_wrongId_when_getMovies_then_returnResponseError() = runTest {
        val response = service.getMovies(id = "wrong_id")
        advanceUntilIdle()
        assertTrue(response.isSuccessful)
        assertThat(response.code(), `is`(200))

        // Validate the Response is True or False
        val result = response.body()?.get("Response")!!
        assertThat(result.asString, `is`("False"))
    }

    @Test
    fun given_rightId_when_getMovies_then_returnResponseSameAsDummy() = runTest {
        val response = service.getMovies(id = "tt3896198")
        advanceUntilIdle()
        assertTrue(response.isSuccessful)
        assertThat(response.code(), `is`(200))

        /*
        val result = ParserResponse.responseMapping(response, ResponseSingle::class)
        assertThat(result, instanceOf(ParserResponse.Success::class.java))
        */
        val result = responseParsing(response, ResponseSingle::class.java)
        val dummy = ClassLoader.getSystemResource("movie_single.json").readText()
        val parsedDummy = Gson().fromJson(dummy, ResponseSingle::class.java)
        assertThat(result.getOrNull(), `is`(parsedDummy))
    }

    @Test
    fun given_wrongId_when_getMovies_then_returnResponseSameAsErrorDummy() = runTest {
        val response = service.getMovies(id = "wrong_id")
        advanceUntilIdle()
        assertTrue(response.isSuccessful)
        assertThat(response.code(), `is`(200))

        val result = responseParsing(response, ResponseSingle::class.java)
        assertThat(result.isFailure, `is`(true))
        assertThat(result.exceptionOrNull()?.message, `is`("Incorrect IMDb ID."))

        val dummy = ClassLoader.getSystemResource("movie_unknown.json").readText()
        val parsedDummy = Gson().fromJson(dummy, JsonObject::class.java)
        assertThat(response.body()?.asJsonObject, `is`(parsedDummy.asJsonObject))
    }

    @Test
    fun given_searchAvengers_when_getMovies_then_returnResponseSameAsDummy() = runTest {
        val response = service.getMovies(search = "avengers", page = 1)
        advanceUntilIdle()
        assertTrue(response.isSuccessful)
        assertThat(response.code(), `is`(200))

        val result = responseParsing(response, ResponseSearchList::class.java)
        assertThat(result.isSuccess, `is`(true))

        val dummy = ClassLoader.getSystemResource("movie_list.json").readText()
        val parsedDummy = Gson().fromJson(dummy, ResponseSearchList::class.java)
        assertThat(result.getOrNull(), `is`(parsedDummy))
    }

    @Test
    fun given_searchUnknown_when_getMovies_then_returnResponseMovieNotFound() = runTest {
        val response = service.getMovies(search = "abogoboga", page = 1)
        advanceUntilIdle()
        assertTrue(response.isSuccessful)
        assertThat(response.code(), `is`(200))

        val result = responseParsing(response, ResponseSearchList::class.java)
        assertThat(result.isFailure, `is`(true))
        assertThat(result.exceptionOrNull()?.message, `is`("Movie not found!"))
    }

    /*NETWORK TROUBLE USE CASE*/
    @Test
    fun givenNoInternet_whenGetMovies_thenReturnNetworkError() = runTest {
        service = OMDBService.create(
            "http://${mockWebServer.hostName}:${mockWebServer.port}",
            omdbKeyInterceptor
        )

        val response = MockResponse()
            .setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)

        mockWebServer.enqueue(response)

        try {
            val result = service.getMovies(id = "tt3896198")
            assertFalse(result.isSuccessful)
        } catch (e: Exception) {
            e.printStackTrace()
            assertThat(e, instanceOf(SocketException::class.java))
        }
        advanceUntilIdle()
    }

    @Test
    fun givenRequestDelay_whenGetMovies_thenReturnTimeout() = runTest {
        service = OMDBService.create(
            "http://${mockWebServer.hostName}:${mockWebServer.port}",
            omdbKeyInterceptor
        )

        val response = MockResponse()
            .setBodyDelay(600, java.util.concurrent.TimeUnit.MINUTES)
            .setHeadersDelay(600, java.util.concurrent.TimeUnit.MINUTES)
        mockWebServer.enqueue(response)

        try {
            val result = service.getMovies(id = "tt3896198")
            assertFalse(result.isSuccessful)
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            assertThat(e, instanceOf(SocketTimeoutException::class.java))
        }
        advanceUntilIdle()
    }
}