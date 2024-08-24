package dev.daniza.draflix.network.model

import com.google.gson.annotations.SerializedName

data class ResponseSearchList(
    @SerializedName("Search") var Search: ArrayList<ResponseSearchListItem> = arrayListOf(),
    @SerializedName("totalResults") var totalResults: String? = null,
    @SerializedName("Response") var Response: String? = null
)