package com.ohayo.core.ui.helper.logger

interface Filter {
    /**
     * @param priority Log priority.
     * @param tag      Tag for log.
     * @param message  Formatted log message.
     * @param t        Accompanying exceptions.
     * @return `true` if the log should be skipped, otherwise `false`.
     * @see timber.log.Timber.Tree.log
     */
    fun skipLog(priority: Int, tag: String?, message: String?, t: Throwable?): Boolean
    fun isLoggable(priority: Int, tag: String?): Boolean
}

class NoFilter : Filter {
    override fun skipLog(priority: Int, tag: String?, message: String?, t: Throwable?) = false

    override fun isLoggable(priority: Int, tag: String?) = true

    companion object {
        @JvmField
        val INSTANCE: Filter = NoFilter()
    }
}

class PriorityFilter(private val minPriority: Int) : Filter {
    override fun skipLog(priority: Int, tag: String?, message: String?, t: Throwable?): Boolean {
        return priority < minPriority
    }

    override fun isLoggable(priority: Int, tag: String?): Boolean {
        return priority >= minPriority
    }
}