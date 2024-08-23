package dev.daniza.draflix.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.daniza.draflix.BuildConfig
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideOMDBService(): OMDBService {
        return OMDBService.create(OMDBKeyInterceptor(BuildConfig.OMDB_API_KEY))
    }
}