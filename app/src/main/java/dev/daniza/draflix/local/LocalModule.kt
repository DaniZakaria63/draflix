package dev.daniza.draflix.local

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LocalModule {

    @Singleton
    @Provides
    fun provideDraflixDatabase(@ApplicationContext context: Context): DraflixDatabase {
        return DraflixDatabase.getInstance(context)
    }
}