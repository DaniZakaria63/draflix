package dev.daniza.draflix.core.interactor.search_movie_with_query

import dev.daniza.draflix.core.repository.MovieRepository
import javax.inject.Inject

class SearchMovieWithQuery @Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(type: String, title: String) = movieRepository.getMovies(type, title)
}