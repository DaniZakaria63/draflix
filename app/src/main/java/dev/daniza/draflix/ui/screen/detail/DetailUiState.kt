package dev.daniza.draflix.ui.screen.detail

import dev.daniza.draflix.network.model.ResponseSingle

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Error(val message: String) : DetailUiState()
    data class Success(val movie: ResponseSingle) : DetailUiState()
}