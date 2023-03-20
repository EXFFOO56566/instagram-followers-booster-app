package com.ohayo.core.helper

import timber.log.Timber

class AppDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format("#Timber (%s:%s)#%s", element.fileName, element.lineNumber, Thread.currentThread().name)
    }
}