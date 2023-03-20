package com.lxquyen.instabooster.data.model.response


import com.google.gson.annotations.SerializedName

data class PackageResponse(
    @SerializedName("data")
    var data: List<Data?>?
) {
    data class Data(
        @SerializedName("createdAt")
        var createdAt: String?,
        @SerializedName("id")
        var id: String?,
        @SerializedName("packageStars")
        var numberOfStars: Int?,
        @SerializedName("packageId")
        var packageId: String?,
        @SerializedName("packageName")
        var packageName: String?,
        @SerializedName("packagePlatform")
        var packagePlatform: String?,
        @SerializedName("packagePricing")
        var packagePricing: Int?,
        @SerializedName("packsBought")
        var packsBought: Int?,
        @SerializedName("__v")
        var v: Int?
    )
}