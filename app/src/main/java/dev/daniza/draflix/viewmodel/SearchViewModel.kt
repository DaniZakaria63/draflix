package dev.daniza.draflix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.daniza.draflix.core.interactor.search_movie_with_query.SearchMovieWithQuery
import dev.daniza.draflix.network.model.ResponseSearchListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMovieWithQuery: SearchMovieWithQuery
) : ViewModel() {
    private val _movieList: MutableStateFlow<PagingData<ResponseSearchListItem>> =
        MutableStateFlow(PagingData.empty())
    val movieListState: StateFlow<PagingData<ResponseSearchListItem>> get() = _movieList

    init {
        searchMovies("")
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            searchMovieWithQuery(query)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect { _movieList.value = it }
        }
    }
}