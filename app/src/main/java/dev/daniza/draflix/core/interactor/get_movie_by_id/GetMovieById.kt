package dev.daniza.draflix.core.interactor.get_movie_by_id

import dev.daniza.draflix.core.repository.MovieRepository
import javax.inject.Inject

class GetMovieById @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(id: String) = movieRepository.getMovieDetail(id)
}