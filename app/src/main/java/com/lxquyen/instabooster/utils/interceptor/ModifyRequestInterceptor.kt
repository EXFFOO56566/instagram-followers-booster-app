package com.lxquyen.instabooster.utils.interceptor

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.lxquyen.instabooster.BuildConfig
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import timber.log.Timber
import java.io.IOException
import java.security.MessageDigest
import javax.inject.Inject

/**
 * Created by Furuichi on 25/07/2022
 */
class ModifyRequestInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalPost = originalRequest.body.bodyToString()
        val hashRequestObject = HashRequestObject()

        val modifyPost: String
        val newPost: String
        val newType: String
        if (originalRequest.body is FormBody) {
            modifyPost = FormBody.Builder()
                .add("hash", hashRequestObject.hash ?: "")
                .add("time", hashRequestObject.time ?: "")
                .add("bundleId", hashRequestObject.bundleId ?: "")
                .build()
                .bodyToString()

            newPost = "$originalPost&$modifyPost"
            newType = "application/x-www-form-urlencoded;charset=UTF-8"
        } else {
            modifyPost = hashRequestObject.toJson()
            newPost = originalPost.jsonMergeJson(modifyPost)
            newType = "application/json; charset=utf-8"
        }

        val newRequest = originalRequest.newBuilder()

        if (originalRequest.method == "PUT") {
            newRequest.put(newPost.toRequestBody(newType.toMediaTypeOrNull()))
        } else {
            newRequest.post(newPost.toRequestBody(newType.toMediaTypeOrNull()))
        }
        return chain.proceed(newRequest.build())
    }

    private fun RequestBody?.bodyToString(): String {
        return try {
            val copy = this
            val buffer = Buffer()
            copy?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            Timber.i("#error - bodyToString : ${e.localizedMessage}")
            ""
        }
    }
}

private fun String.jsonMergeJson(modify: String): String {
    return this.replace("}", ",").plus(modify.replace("{", ""))
}

class HashRequestObject {
    @SerializedName("hash")
    var hash: String? = null

    @SerializedName("time")
    var time: String? = null

    @SerializedName("bundleId")
    var bundleId: String? = null

    init {
        val unixtime = System.currentTimeMillis() / 1000L
        val tmp = "$unixtime"
            .plus("|")
            .plus(BuildConfig.API_KEY)

        bundleId = BuildConfig.APPLICATION_ID
        time = unixtime.toString()
        hash = tmp.hash256()
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    private fun String.hash256(): String {
        val bytes = this.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

}