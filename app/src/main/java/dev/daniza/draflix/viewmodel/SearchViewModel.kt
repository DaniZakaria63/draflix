package dev.daniza.draflix.viewmodel

import dev.daniza.draflix.core.repository.MovieRepository
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) {
}