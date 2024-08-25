package dev.daniza.draflix.core

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dev.daniza.draflix.core.repository.MovieRepository
import dev.daniza.draflix.core.repository.MovieRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class CodeModule {
    @Binds
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository
}