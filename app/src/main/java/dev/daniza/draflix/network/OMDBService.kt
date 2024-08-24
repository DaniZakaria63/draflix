package dev.daniza.draflix.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import dev.daniza.draflix.BuildConfig
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
    suspend fun getMovies(
        @Query("i") id: String? = null,
        @Query("t") title: String? = null,
        @Query("s") search: String? = null,
        @Query("y") year: String? = null,
        @Query("plot") plot: String? = null,
        @Query("type") type: String? = null,
        @Query("page") page: Int? = null,
        @Query("callback") callback: String? = null,
    ): Response<JsonObject>

    companion object {
        fun create(baseUrl: String, keyAccessInterceptor: Interceptor): OMDBService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder().apply {
                addInterceptor(keyAccessInterceptor)
                if (BuildConfig.DEBUG) addInterceptor(logger)
            }.build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
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