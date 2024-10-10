package dev.daniza.draflix.ui.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            ErrorScreen((movieState as DetailUiState.Error).message) {
                requestAttemptCount++
            }
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
            .padding(top = 32.dp)
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(data.Poster.orEmpty())
                .crossfade(true)
                .build(),
            contentDescription = "movie-${data.imdbID}",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
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
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.Title.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.sp
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = data.imdbRating.orEmpty(), color = Color.Black, fontSize = 14.sp)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.Actors.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = data.Genre.orEmpty(),
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(text = data.Plot.orEmpty(), color = Color.Black, fontSize = 14.sp)
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