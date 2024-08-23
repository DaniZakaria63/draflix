package dev.daniza.draflix.network

import dev.daniza.draflix.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class OMDBKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newUrl = originalRequest.url.newBuilder()
            .addQueryParameter("apikey", BuildConfig.OMDB_API_KEY)
            .build()
        return originalRequest.newBuilder().url(newUrl).build().let { chain.proceed(it) }
    }
}