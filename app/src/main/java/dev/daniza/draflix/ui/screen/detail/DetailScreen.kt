package dev.daniza.draflix.ui.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.daniza.draflix.viewmodel.DetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailScreen(
    movieId: String,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val movieState by viewModel.movieState.collectAsState()
    val requestAttemptCount by remember { mutableIntStateOf(0) }
    val detailCoroutineScope = rememberCoroutineScope()

    LaunchedEffect(requestAttemptCount) {
        detailCoroutineScope.launch { viewModel.getMovieDetail(movieId) }
    }

    if (movieState == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "Loading")
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        GlideImage(
            model = movieState?.Poster,
            contentDescription = "image.of.${movieId}"
        )
    }
}