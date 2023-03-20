package com.ohayo.core.ui.helper.logger

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.util.SparseArray
import java.text.SimpleDateFormat

interface Formatter {
    fun format(priority: Int, tag: String?, message: String): String
}

class NoTagFormatter private constructor() : Formatter {
    override fun format(priority: Int, tag: String?, message: String) = message.trimIndent()

    companion object {
        @JvmField
        val INSTANCE = NoTagFormatter()
    }
}

class DefaultLogFormatter private constructor() : Formatter {
    private val prioPrefixes = SparseArray<String>()
    override fun format(priority: Int, tag: String?, message: String): String {
        val time = System.currentTimeMillis().toTimeString()
        val newPriority = prioPrefixes[priority] ?: ""
        return "$time -- $newPriority${tag ?: "[QuyenLX_TAG]:"} $message\n"
    }

    companion object {
        val INSTANCE = DefaultLogFormatter()
    }

    init {
        prioPrefixes.append(Log.VERBOSE, "V/")
        prioPrefixes.append(Log.DEBUG, "D/")
        prioPrefixes.append(Log.INFO, "I/")
        prioPrefixes.append(Log.WARN, "W/")
        prioPrefixes.append(Log.ERROR, "E/")
        prioPrefixes.append(Log.ASSERT, "WTF/")
    }
}

@SuppressLint("SimpleDateFormat")
fun Long.toTimeString(): String {
    val pattern = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    } else {
        "yyyy-MM-dd'T'HH:mm:ss.SSS"
    }

    return SimpleDateFormat(pattern).format(this)
}