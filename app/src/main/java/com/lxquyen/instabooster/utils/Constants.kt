package com.lxquyen.instabooster.utils

import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Created by Furuichi on 07/07/2022
 */
object Constants {
    const val BASE_URL = "http://104.198.60.144:3003"

    const val FEATURE_FLAG_SHOW_NATIVE_AD = true
}

object PrefKeys {
    val USER_ID = stringPreferencesKey("USER_ID")
    val FEED_ID_TMP = stringPreferencesKey("FEED_ID_TMP")
}
