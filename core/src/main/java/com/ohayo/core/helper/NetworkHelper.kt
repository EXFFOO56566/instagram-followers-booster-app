package com.ohayo.core.helper

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ohayo.core.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor

object NetworkHelper {
    val gson: Gson by lazy {
        return@lazy GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("yyyy-mm-dd'T'HH:mm:ss.sssZ")
            .setPrettyPrinting()
            .create()
    }

    val logger: HttpLoggingInterceptor by lazy {
        return@lazy HttpLoggingInterceptor()
            .also {
                val level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }

                it.setLevel(level)
            }
    }
}