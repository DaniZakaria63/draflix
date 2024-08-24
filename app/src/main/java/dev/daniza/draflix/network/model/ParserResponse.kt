package dev.daniza.draflix.network.model

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response

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