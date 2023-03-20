package com.lxquyen.instabooster.data.model.response


import com.google.gson.annotations.SerializedName
import com.lxquyen.instabooster.data.model.Boost

data class BoostResponse(
    @SerializedName("data")
    var data: List<Data>?
) {
    data class Data(
        @SerializedName("boostOfFollower")
        var boostOfFollower: Int?,
        @SerializedName("boostStar")
        var boostStar: Int?,
        @SerializedName("boostUsed")
        var boostUsed: Int?,
        @SerializedName("createdAt")
        var createdAt: String?,
        @SerializedName("id")
        var id: String,
        @SerializedName("__v")
        var v: Int?
    )
}

fun List<BoostResponse.Data>.mapToBoosts(): List<Boost> {
    return Boost.fromApi(this)
}