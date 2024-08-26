package dev.daniza.draflix.ui.screen.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.daniza.draflix.network.model.ResponseSearchListItem
import dev.daniza.draflix.ui.screen.component.ErrorScreen
import dev.daniza.draflix.ui.screen.component.LoadingItemRectangle
import dev.daniza.draflix.utilities.ConnectionState
import dev.daniza.draflix.utilities.DEFAULT_PARAM_TYPE
import dev.daniza.draflix.utilities.connectivityState
import dev.daniza.draflix.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeListScreen(
    onMovieClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val moviesPagingItems = viewModel.movieListState.collectAsLazyPagingItems()
    val requestState by viewModel.requestState.collectAsState(initial = HomeListState.Loading)
    val searchCoroutineScope = rememberCoroutineScope()
    var searchedTitleText by remember { mutableStateOf("") }
    var searchedTypeText by remember { mutableStateOf(DEFAULT_PARAM_TYPE.first()) }
    val connectionStatus by connectivityState()
    val showShimmer by remember {
        derivedStateOf { requestState is HomeListState.Loading }
    }

    LaunchedEffect(key1 = searchedTitleText, key2 = searchedTypeText) {
        searchCoroutineScope.launch {
            viewModel.searchMovies(searchedTypeText, searchedTitleText)
        }
    }

    LaunchedEffect(key1 = connectionStatus) {
        searchCoroutineScope.launch {
            val hasInternet = connectionStatus == ConnectionState.Available
            val hasData = moviesPagingItems.itemCount > 0
            val state = when {
                hasInternet && hasData -> HomeListState.Success
                hasInternet && !hasData -> HomeListState.NoData
                moviesPagingItems.loadState.append is LoadState.Error -> HomeListState.Error
                else -> HomeListState.NoInternet
            }
            viewModel.setState(state)
        }
    }

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        topBar = {
            DraflixTopBar()
        },
    ) { paddingValues ->
        if (requestState is HomeListState.NoInternet) {
            ListNoInternetScreen()
        }

        if (moviesPagingItems.loadState.refresh is LoadState.Error) {
            ErrorScreen {
                searchCoroutineScope.launch {
                    moviesPagingItems.retry()
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            item(span = { GridItemSpan(2) }) {
                Text(text = "Search")
            }

            item(span = { GridItemSpan(2) }) {
                SearchTextField(
                    value = searchedTitleText,
                    onValueChange = { searchedTitleText = it },
                )
            }

            item(span = { GridItemSpan(2) }) {
                Row(modifier = Modifier.padding(8.dp)) {
                    DEFAULT_PARAM_TYPE.forEach { type ->
                        MovieTypeTab(
                            text = type,
                            selected = searchedTypeText == type,
                            onSelect = {
                                searchedTypeText = type
                            }
                        )
                    }
                }
            }

            if (moviesPagingItems.loadState.refresh is LoadState.Loading) {
                items(10) {
                    ListLoadingScreen(showShimmer = showShimmer)
                }
            }

            items(moviesPagingItems.itemCount) { index ->
                moviesPagingItems[index]?.let {
                    MovieItemCard(
                        movie = it,
                        onMovieClick = onMovieClick
                    )
                } ?: ListNoDataScreen()
            }
        }
    }
}

@Composable
fun ListNoDataScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Pencarian Tidak Ditemukan")
    }
}

@Composable
fun ListLoadingScreen(
    showShimmer: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingItemRectangle(
            showShimmer = showShimmer,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun ListNoInternetScreen() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(125.dp)
            .background(MaterialTheme.colorScheme.error),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No Internet Connection",
            color = MaterialTheme.colorScheme.onError

        )
    }
}

@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyMedium,
        label = { Text(text = "Search") },
        placeholder = { Text(text = "Cari judul film...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "search.icon"
            )
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    )
}

@Composable
fun MovieTypeTab(
    text: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Text(
        text = text,
        modifier = Modifier
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
            )
            .clickable(onClick = onSelect)
            .padding(16.dp)
    )
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
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        scrollBehavior = scrollBehavior
    )
}

@Preview
@Composable
fun ShimmerLoadingPreview() {
    ListLoadingScreen(showShimmer = true)
}