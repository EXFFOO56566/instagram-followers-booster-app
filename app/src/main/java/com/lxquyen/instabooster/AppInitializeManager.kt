package com.lxquyen.instabooster

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.util.Log
import com.ohayo.core.di.ApplicationScope
import com.ohayo.core.helper.AppDebugTree
import com.ohayo.core.ui.helper.logger.FileLoggerTree
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Furuichi on 7/7/2022
 */
class AppInitializeManager @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope
) {

    @Inject
    lateinit var timberInit: TimberInit

    fun run() {
        merge(
            timberInit.run()
        ).launchIn(externalScope)
    }
}

class TimberInit @Inject constructor(
    @ApplicationContext private val appContext: Context
) {

    fun run(): Flow<Unit> {
        return flow {
            if (BuildConfig.DEBUG) {
                Timber.plant(AppDebugTree())
            } else {
                appContext.externalCacheDir?.let { file ->
                    if (!file.exists()) {
                        file.mkdirs()
                    }
                    val fileLoggerTree = FileLoggerTree.Builder()
                        .withFileName("logcat_%g.log")
                        .withDirName(file.path)
                        .withMinPriority(Log.VERBOSE)
                        .appendToFile(true)
                        .build()
                    Timber.plant(fileLoggerTree)
                }
            }
        }
    }
}