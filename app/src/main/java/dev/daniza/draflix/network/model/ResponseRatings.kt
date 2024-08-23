package dev.daniza.draflix.network.model

import com.google.gson.annotations.SerializedName

data class ResponseRatings(
    @SerializedName("Source") var Source: String? = null,
    @SerializedName("Value") var Value: String? = null
)