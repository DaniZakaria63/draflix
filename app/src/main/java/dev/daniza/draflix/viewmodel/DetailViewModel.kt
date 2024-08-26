package dev.daniza.draflix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.daniza.draflix.core.interactor.get_movie_by_id.GetMovieById
import dev.daniza.draflix.network.model.ResponseSingle
import dev.daniza.draflix.ui.screen.detail.DetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getMovieById: GetMovieById
) : ViewModel() {
    private val _movieState: MutableStateFlow<DetailUiState> =
        MutableStateFlow(DetailUiState.Loading)
    val movieState: StateFlow<DetailUiState?> get() = _movieState.asStateFlow()

    fun getMovieDetail(id: String) {
        viewModelScope.launch {
            val result = getMovieById(id)
            if (result.isSuccess) {
                _movieState.emit(DetailUiState.Success(result.getOrDefault(ResponseSingle())))
            } else {
                _movieState.emit(DetailUiState.Error(result.exceptionOrNull()?.message.orEmpty()))
            }
        }
    }
}