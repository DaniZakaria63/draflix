package dev.daniza.draflix.core

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.daniza.draflix.core.repository.MovieRepository
import dev.daniza.draflix.core.repository.MovieRepositoryImpl

@Module
@InstallIn(ActivityComponent::class)
abstract class CodeModule {
    @Binds
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository
}