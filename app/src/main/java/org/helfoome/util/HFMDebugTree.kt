package org.helfoome.util

import timber.log.Timber

class HFMDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return "${element.fileName}:${element.lineNumber}#${element.methodName}"
    }
}
