package dev.daniza.draflix.network

import okhttp3.Interceptor
import okhttp3.Response

class OMDBKeyInterceptor(private val keyAccess: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newUrl = originalRequest.url.newBuilder()
            .addQueryParameter("apikey", keyAccess)
            .build()
        return originalRequest.newBuilder().url(newUrl).build().let { chain.proceed(it) }
    }
}