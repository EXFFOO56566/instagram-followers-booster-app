package com.ohayo.core.ui.helper.logger

import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

class NoTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) = Unit
}

open class PriorityTree(priority: Int) : Timber.DebugTree() {

    val defaultFormatter: Formatter
        get() = DefaultLogFormatter.INSTANCE

    private val priorityFilter = PriorityFilter(priority)
    var filter = NoFilter.INSTANCE
        private set

    /**
     * Add additional [Filter]
     *
     * @param f Filter
     * @return itself
     */

    fun withFilter(f: Filter): PriorityTree {
        filter = f
        return this
    }

    protected fun format(priority: Int, tag: String?, message: String): String {
        return defaultFormatter.format(priority, tag, message)
    }

    override fun isLoggable(priority: Int): Boolean {
        return isLoggable("", priority)
    }

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return priorityFilter.isLoggable(priority, tag) && filter.isLoggable(priority, tag)
    }

    /**
     * Use the additional filter to determine if this log needs to be skipped
     *
     * @param priority Log priority
     * @param tag      Log tag
     * @param message  Log message
     * @param t        Log throwable
     * @return true if needed to be skipped or false
     */
    protected fun skipLog(priority: Int, tag: String?, message: String, t: Throwable?): Boolean {
        return filter.skipLog(priority, tag, message, t)
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, format(priority, tag, message), t)
    }
}

/**
 * @param priority    Priority from which to log
 * @param logger      [java.util.logging.Logger] used for logging
 * @param fileHandler [java.util.logging.FileHandler] used for logging
 * @param path        Base path of file
 * @param nbFiles     Max number of files
 */
class FileLoggerTree constructor(
    priority: Int,
    private val logger: Logger,
    private val fileHandler: FileHandler?,
    private val path: String,
    private val nbFiles: Int
) : PriorityTree(priority) {

    override fun createStackElementTag(element: StackTraceElement): String? {
        val className = super.createStackElementTag(element)?.split("$")?.get(0)
        return "($className.kt:${element.lineNumber})#${element.methodName}"
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (skipLog(priority, tag, message, t)) return
        logger.log(fromPriorityToLevel(priority), format(priority, tag, message))
        if (t != null) {
            logger.log(fromPriorityToLevel(priority), "", t)
        }
    }

    /**
     * Delete all log files
     */
    fun clear() {
        fileHandler?.close()
        (0 until nbFiles).map { File(getFileName(it)) }.filter { it.exists() }
            .forEach { it.delete() }
    }

    /**
     * Return the file name corresponding to the number
     *
     * @param i Number of file
     * @return Real file name
     */
    fun getFileName(i: Int) = if (!this.path.contains("%g")) {
        this.path + "." + i
    } else {
        this.path.replace("%g", "" + i)
    }

    /**
     * @return All files created by the logger
     */
    val files: List<File>
        get() = (0 until nbFiles).map { File(getFileName(it)) }.filter { it.exists() }

    private fun fromPriorityToLevel(priority: Int) = when (priority) {
        Log.VERBOSE -> Level.FINER
        Log.DEBUG -> Level.FINE
        Log.INFO -> Level.INFO
        Log.WARN -> Level.WARNING
        Log.ERROR -> Level.SEVERE
        Log.ASSERT -> Level.SEVERE
        else -> Level.FINEST
    }

    class Builder {
        private var fileName = "log"
        private var dir = ""
        private var priority = Log.INFO
        private var sizeLimit = SIZE_LIMIT
        private var fileLimit = NB_FILE_LIMIT
        private var appendToFile = true

        /**
         * Specify a custom file name
         *
         *  Default file name is "log" which will result in log.0, log.1, log.2...
         *
         * @param fn File name
         * @return itself
         */
        fun withFileName(fn: String): Builder {
            fileName = fn
            return this
        }

        /**
         * Specify a custom dir name
         *
         * @param dn Dir name
         * @return itself
         */
        fun withDirName(dn: String): Builder {
            dir = dn
            return this
        }

        /**
         * Specify a custom dir name
         *
         * @param d Dir file
         * @return itself
         */
        fun withDir(d: File): Builder {
            dir = d.absolutePath
            return this
        }

        /**
         * Specify a priority from which it can start logging
         *
         *  Default is [Log.INFO]
         *
         * @param p priority
         * @return itself
         */
        fun withMinPriority(p: Int): Builder {
            priority = p
            return this
        }

        /**
         * Specify a custom file size limit
         *
         *  Default is 1048576 bytes
         *
         * @param nbBytes Custom size limit in bytes
         * @return itself
         */
        fun withSizeLimit(nbBytes: Int): Builder {
            sizeLimit = nbBytes
            return this
        }

        /**
         * Specify a custom file number limit
         *
         *  Default is 3
         *
         * @param f Max number of files to use
         * @return itself
         */
        fun withFileLimit(f: Int): Builder {
            fileLimit = f
            return this
        }

        /**
         * Specify an option for [FileHandler] creation
         *
         * @param b true to append to existing file
         * @return itself
         */
        fun appendToFile(b: Boolean): Builder {
            appendToFile = b
            return this
        }

        /**
         * Create the file logger tree with options specified.
         *
         * @return [FileLoggerTree]
         * @throws IOException if file creation fails
         */
        @Throws(IOException::class)
        fun build(): FileLoggerTree {
            val path = "$dir/$fileName"
            val fileHandler: FileHandler
            val logger = MyLogger.getLogger(TAG)
            logger.level = Level.ALL
            if (logger.handlers.isEmpty()) {
                fileHandler = FileHandler(path, sizeLimit, fileLimit, appendToFile)
                fileHandler.formatter = NoFormatter()
                logger.addHandler(fileHandler)
            } else {
                fileHandler = logger.handlers[0] as FileHandler
            }

            return FileLoggerTree(priority, logger, fileHandler, path, fileLimit)
        }

        /**
         * Create the file logger tree with options specified.
         *
         * @return [FileLoggerTree] or [NoTree] if an exception occurred
         */
        fun buildQuietly() = kotlin.runCatching { build() }.getOrNull() ?: NoTree()

        companion object {
            private const val SIZE_LIMIT = 5 * 1024 * 1024
            private const val NB_FILE_LIMIT = 5
        }
    }

    private class NoFormatter : java.util.logging.Formatter() {
        override fun format(record: LogRecord): String = record.message
    }

    /**
     * Constructs a `Logger` object with the supplied name and resource
     * bundle name; `notifyParentHandlers` is set to `true`.
     *
     *
     * Notice : Loggers use a naming hierarchy. Thus "z.x.y" is a child of "z.x".
     *
     * @param name the name of this logger, may be `null` for anonymous
     * loggers.
     */
    private class MyLogger
    private constructor(name: String?) : Logger(name, null) {
        companion object {
            fun getLogger(name: String?) = MyLogger(name)
        }
    }

    companion object {
        private const val TAG = "FileLoggerTree"
    }
}