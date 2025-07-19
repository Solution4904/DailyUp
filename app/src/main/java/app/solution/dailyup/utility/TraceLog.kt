package app.solution.dailyup.utility

import android.util.Log

object TraceLog {
    operator fun invoke(tag: String = "DEBUG", message: String) {
        val stack = Throwable().stackTrace[2]
        val logMsg = "(${stack.fileName}:${stack.lineNumber}) $message"
        Log.d(tag, logMsg)
    }
}