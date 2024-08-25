package dev.daniza.draflix.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.daniza.draflix.BuildConfig
import dev.daniza.draflix.utilities.OMDB_BASE_URL
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideOMDBService(): OMDBService {
        return OMDBService.create(OMDB_BASE_URL, OMDBKeyInterceptor(BuildConfig.OMDB_API_KEY))
    }
}