package dev.daniza.draflix.network

import dev.daniza.draflix.BuildConfig
import dev.daniza.draflix.utilities.OMDB_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface OMDBService {

    companion object {
        fun create(): OMDBService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder().apply {
                addInterceptor(OMDBKeyInterceptor())
                if (BuildConfig.DEBUG) addInterceptor(logger)
            }.build()

            return Retrofit.Builder()
                .baseUrl(OMDB_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OMDBService::class.java)
        }
    }
}