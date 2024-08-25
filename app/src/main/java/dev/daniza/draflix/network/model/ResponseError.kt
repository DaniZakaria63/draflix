package dev.daniza.draflix.network.model

import com.google.gson.annotations.SerializedName

data class ResponseError(
    @SerializedName("Response") var Response: String? = null,
    @SerializedName("Error") var Error: String? = null
)