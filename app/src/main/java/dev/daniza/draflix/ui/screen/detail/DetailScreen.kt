package dev.daniza.draflix.ui.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import dev.daniza.draflix.R
import dev.daniza.draflix.network.model.ResponseSingle
import dev.daniza.draflix.ui.screen.component.ErrorScreen
import dev.daniza.draflix.ui.screen.component.LoadingItemRectangle
import dev.daniza.draflix.viewmodel.DetailViewModel
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    movieId: String,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val movieState by viewModel.movieState.collectAsState()
    var requestAttemptCount by remember { mutableIntStateOf(0) }
    val detailCoroutineScope = rememberCoroutineScope()

    LaunchedEffect(requestAttemptCount) {
        detailCoroutineScope.launch {
            viewModel.getMovieDetail(movieId)
        }
    }

    when (movieState) {
        is DetailUiState.Success -> DetailMovieScreen(
            data = (movieState as DetailUiState.Success).movie
        )

        is DetailUiState.Error -> {
            ErrorScreen { requestAttemptCount++ }
        }

        else -> DetailLoadingScreen()
    }
}

@Composable
fun DetailMovieScreen(
    data: ResponseSingle
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(12.dp)
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(data.Poster.orEmpty())
                .crossfade(true)
                .build(),
            contentDescription = "picture of movie-${data.imdbID}",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(5.dp))
        ) {
            val state = painter.state
            when (state) {
                is AsyncImagePainter.State.Loading -> {
                    LoadingItemRectangle(
                        showShimmer = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is AsyncImagePainter.State.Error -> {
                    ImageBitmap.imageResource(id = R.drawable.img_error)
                }

                else -> SubcomposeAsyncImageContent()
            }
        }
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = data.Title.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DetailLoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}