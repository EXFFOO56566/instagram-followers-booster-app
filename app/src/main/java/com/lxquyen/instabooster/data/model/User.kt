package com.lxquyen.instabooster.data.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.lxquyen.instabooster.utils.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("followers")
    var followers: Int?,
    @SerializedName("followings")
    var followings: Int?,
    @SerializedName("bio")
    var bio: String?,
    @SerializedName("fullName")
    var fullName: String?,
    @SerializedName("id")
    var id: String,
    @SerializedName("isPrivate")
    var isPrivate: Boolean?,
    @SerializedName("isVerified")
    var isVerified: Boolean?,
    @SerializedName("pk")
    var pk: String?,
    @SerializedName("stars")
    var stars: Int?,
    @SerializedName("username")
    var username: String?,
    @SerializedName("__v")
    var v: Int?,
    @SerializedName("isFirstLogin")
    var isFirstLogin: Boolean
) : Parcelable {
    val followersText: String
        get() {
            val followers = followers ?: 0
            return if (followers > 1000) {
                "1000+"
            } else {
                "$followers"
            }
        }

    val followingsText: String
        get() {
            val followings = followings ?: 0
            return if (followings > 1000) {
                "1000+"
            } else {
                "$followings"
            }
        }

    val profilePicUrl: String
        get() = Constants.BASE_URL + "instagram/api/$pk.png"

    val displayName: String?
        get() {
            return if (fullName.isNullOrEmpty())
                username
            else
                fullName

        }
}