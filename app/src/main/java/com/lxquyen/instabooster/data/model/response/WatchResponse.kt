package com.lxquyen.instabooster.data.model.response

import com.google.gson.annotations.SerializedName

data class WatchResponse(
    @SerializedName("data")
    var `data`: Data?
) {
    data class Data(
        @SerializedName("createdAt")
        var createdAt: String?,
        @SerializedName("id")
        var id: String?,
        @SerializedName("reward")
        var reward: Int?,
        @SerializedName("userId")
        var userId: String?,
        @SerializedName("__v")
        var v: Int?
    )
}