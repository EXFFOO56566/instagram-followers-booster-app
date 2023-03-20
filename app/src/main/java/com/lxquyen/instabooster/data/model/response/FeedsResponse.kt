package com.lxquyen.instabooster.data.model.response

import com.google.gson.annotations.SerializedName
import com.lxquyen.instabooster.data.model.User

data class FeedsResponse(
    @SerializedName("data")
    val users: List<User>?
)