package dev.daniza.draflix.ui.screen.detail

import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import dev.daniza.draflix.network.model.ResponseSingle
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDetailMovieScreenWithDummyResponse() {
        val dummyResponseSingle = ResponseSingle(
            imdbID = "999",
            Title = "Nothing Important",
            imdbRating = "8.0",
            Actors = "No one, Giraffe, Month",
            Plot = "The first colonize was the ants",
            Genre = "Documentary"
        )
        composeTestRule.setContent {
            DetailMovieScreen(dummyResponseSingle)
        }
        composeTestRule.onRoot().printToLog("detailMovieScreenTest")
        composeTestRule.onNodeWithContentDescription(
            useUnmergedTree = true,
            label = "movie-999",
        ).assertIsNotDisplayed()
    }
}