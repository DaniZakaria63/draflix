package dev.daniza.draflix.local

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.daniza.draflix.local.dao.MovieDao
import dev.daniza.draflix.local.dao.RemoteKeysDao
import dev.daniza.draflix.local.dao.SearchDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    @Singleton
    @Provides
    fun provideDraflixDatabase(@ApplicationContext context: Context): DraflixDatabase {
        return DraflixDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideMovieDao(draflixDatabase: DraflixDatabase): MovieDao = draflixDatabase.movieDao()

    @Singleton
    @Provides
    fun provideSearchDao(draflixDatabase: DraflixDatabase): SearchDao = draflixDatabase.searchDao()

    @Singleton
    @Provides
    fun provideRemoteKeysDao(draflixDatabase: DraflixDatabase): RemoteKeysDao =
        draflixDatabase.remoteKeysDao()
}