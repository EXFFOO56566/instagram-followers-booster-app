package com.lxquyen.instabooster

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Created by Furuichi on 07/07/2022
 */
@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var appInit: AppInitializeManager

    override fun onCreate() {
        super.onCreate()
        appInit.run()
    }
}

