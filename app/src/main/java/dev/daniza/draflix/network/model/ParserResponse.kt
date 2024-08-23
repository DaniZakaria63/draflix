package dev.daniza.draflix.network.model

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import kotlin.reflect.KClass

sealed class ParserResponse<out T> {
    data class Success<out T>(val value: T) : ParserResponse<T>()
    data class Error(val message: String) : ParserResponse<ResponseError>()

    companion object {
        fun <T : Any> responseMapping(
            response: Response<JsonObject>,
            target: KClass<T>
        ): ParserResponse<Any> {
            if (!response.isSuccessful) return Error(response.message())

            val body = response.body()?.asJsonObject
            val result = body?.get("Response")
            return if (result?.asString == "True") {
                val data = Gson().fromJson(body, target::class.java)
                Success(data)
            } else {
                Error(body?.get("Error")?.asString?.ifEmpty { "Unknown Error" }.toString())
            }
        }
    }

    inline fun onSuccess(block: (T) -> Unit): ParserResponse<T> {
        if (this is Success) block(this.value)
        return this
    }

    inline fun onError(block: (ResponseError) -> Unit): ParserResponse<T> {
        if (this is Error) block(ResponseError(message))
        return this
    }
}

fun <T : Any> responseParsing(response: Response<JsonObject>, type: Class<T>): Result<T> {
    val body = response.body()?.asJsonObject
    val result = body?.get("Response")
    if (!response.isSuccessful || result?.asString == "False") {
        return Result.failure(
            Throwable(
                body?.get("Error")?.asString?.ifEmpty { "Unknown Error" }.toString()
            )
        )
    }
    val data = Gson().fromJson(body, type)
    return Result.success(data)
}