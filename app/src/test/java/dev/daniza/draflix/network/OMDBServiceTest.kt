package dev.daniza.draflix.network

import com.google.gson.Gson
import dev.daniza.draflix.network.model.ResponseSingle
import dev.daniza.draflix.network.model.responseParsing
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OMDBServiceTest {
    private lateinit var service: OMDBService
    private val realKeyAccess = "10a5e46b"
    private val fakeKeyAccess = "fake_key_access"
    private var omdbKeyInterceptor = OMDBKeyInterceptor(keyAccess = realKeyAccess)

    @Before
    fun setup() {
        service = OMDBService.create(omdbKeyInterceptor)
    }

    @Test
    fun should_get_success_response() = runTest {
        val response = service.getMovieDetail(id = "tt3896198")
        advanceUntilIdle()
        assertTrue(response.isSuccessful)
        assertThat(response.code(), `is`(200))
    }

    @Test
    fun should_get_error_response() = runTest {
        omdbKeyInterceptor = OMDBKeyInterceptor(keyAccess = fakeKeyAccess)
        service = OMDBService.create(omdbKeyInterceptor)
        val response = service.getMovieDetail(id = "tt3896198")
        advanceUntilIdle()
        assertFalse(response.isSuccessful)
        assertThat(response.code(), `is`(401))
    }

    @Test
    fun given_rightId_when_getMovieDetail_then_returnResponseSingle() = runTest {
        val response = service.getMovieDetail(id = "tt3896198")
        advanceUntilIdle()
        assertTrue(response.isSuccessful)
        assertThat(response.code(), `is`(200))

        // Validate the Response is True or False
        val result = response.body()?.get("Response")!!
        assertThat(result.asString, `is`("True"))
    }

    @Test
    fun given_wrongId_when_getMovieDetail_then_returnResponseError() = runTest {
        val response = service.getMovieDetail(id = "wrong_id")
        advanceUntilIdle()
        assertTrue(response.isSuccessful)
        assertThat(response.code(), `is`(200))

        // Validate the Response is True or False
        val result = response.body()?.get("Response")!!
        assertThat(result.asString, `is`("False"))
    }

    @Test
    fun given_rightId_when_getMovieDetail_then_returnResponseSameAsDummy() = runTest {
        val response = service.getMovieDetail(id = "tt3896198")
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
}