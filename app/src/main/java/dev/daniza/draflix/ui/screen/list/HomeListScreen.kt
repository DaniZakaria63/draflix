package dev.daniza.draflix.ui.screen.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.daniza.draflix.network.model.ResponseSearchListItem
import dev.daniza.draflix.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeListScreen(
    onMovieClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val moviesPagingItems = viewModel.movieListState.collectAsLazyPagingItems()
    var searchCoroutineScope = rememberCoroutineScope()
    var searchedTitleText by remember { mutableStateOf("Cari judul film...") }
    var searchedTypeText by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        topBar = {
            DraflixTopBar()
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                item(span = { GridItemSpan(2) }) {
                    BasicTextField(
                        value = searchedTitleText,
                        onValueChange = {
                            searchedTitleText = it
                            searchCoroutineScope.launch {
                                viewModel.searchMovies()
                            }
                        },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                items(moviesPagingItems.itemCount) { index ->
                    moviesPagingItems[index]?.let {
                        MovieItemCard(
                            movie = it,
                            onMovieClick = onMovieClick
                        )
                    }
                }

                val loadState = moviesPagingItems.loadState
                item(span = { GridItemSpan(2) }) {
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
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MovieItemCard(
    movie: ResponseSearchListItem,
    modifier: Modifier = Modifier,
    onMovieClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable {
                    onMovieClick(movie.imdbID.orEmpty())
                }
        ) {
            GlideImage(
                model = movie.Poster.orEmpty(),
                contentDescription = "picture of movie-${movie.imdbID}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(5.dp))
            )

            Text(
                text = movie.Title.orEmpty(),
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraflixTopBar() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    CenterAlignedTopAppBar(
        title = {
            Text(text = "Draflix")
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        scrollBehavior = scrollBehavior
    )
}