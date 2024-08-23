package dev.daniza.draflix.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import dev.daniza.draflix.BuildConfig
import dev.daniza.draflix.utilities.OMDB_BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OMDBService {
    @GET("/")
    suspend fun getMovieDetail(
        @Query("i") id: String
    ): Response<JsonObject>

    companion object {
        fun create(keyAccessInterceptor: Interceptor): OMDBService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder().apply {
                addInterceptor(keyAccessInterceptor)
                if (BuildConfig.DEBUG) addInterceptor(logger)
            }.build()

            return Retrofit.Builder()
                .baseUrl(OMDB_BASE_URL)
                .client(client)
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().setLenient().create()
                    )
                )
                .build()
                .create(OMDBService::class.java)
        }
    }
}