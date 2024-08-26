package dev.daniza.draflix.ui.screen.list

sealed class HomeListState {
    object Loading : HomeListState()
    object NoData : HomeListState()
    object NoInternet : HomeListState()
    object Success : HomeListState()
    data class Error(val message: String) : HomeListState()
}