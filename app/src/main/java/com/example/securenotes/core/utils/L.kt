package com.example.securenotes.core.utils

import android.util.Log
import java.io.IOException

object L {
    const val TAG = "SecureNotes"
    var logcatProc: Process? = null
        private set

    fun setup() {
        Thread {
            try {
                logcatProc =
                    Runtime.getRuntime().exec("logcat -v time $TAG:V *:E")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    @JvmStatic
    fun i(message: String?) {
        Log.i(TAG, message!!)
    }

    @JvmStatic
    fun e(message: String?) {
        Log.e(TAG, message!!)
    }

    @JvmStatic
    fun e(message: String?, e: Throwable?) {
        Log.e(TAG, message, e)
    }
}