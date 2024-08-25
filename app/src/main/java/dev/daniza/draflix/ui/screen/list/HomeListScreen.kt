package dev.daniza.draflix.ui.screen.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.daniza.draflix.viewmodel.SearchViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HomeListScreen(
    onMovieClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val moviesPagingItems = viewModel.movieListState.collectAsLazyPagingItems()
    LazyColumn(
        modifier = modifier.padding(16.dp)
    ) {
        items(moviesPagingItems.itemCount) { index ->
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onMovieClick(moviesPagingItems[index]?.imdbID.orEmpty())
                }
            ) {
                GlideImage(
                    model = moviesPagingItems[index]?.Poster,
                    contentDescription = "picture of movie-${index}",
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(35.dp)
                )
                Text(
                    text = moviesPagingItems[index]?.Title.orEmpty(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider()
        }
        val loadState = moviesPagingItems.loadState
        item {
            when {
                loadState.refresh is LoadState.Loading -> {
                    Text(text = "Loading In Refresh")
                }

                loadState.refresh is LoadState.Error -> {
                    Text(text = "Error happened In Refresh")
                }

                loadState.append is LoadState.Loading -> {
                    Text(text = "Loading In Append")
                }

                loadState.append is LoadState.Error -> {
                    Text(text = "Error happened In Append")
                }
            }
        }
    }
}