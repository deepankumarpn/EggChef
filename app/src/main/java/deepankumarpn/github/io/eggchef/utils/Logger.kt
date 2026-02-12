package deepankumarpn.github.io.eggchef.utils

import android.util.Log
import deepankumarpn.github.io.eggchef.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object Logger {

    private const val DEFAULT_TAG = "EggChef"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO.limitedParallelism(1))

    inline fun ifDebug(action: () -> Unit) = if (BuildConfig.DEBUG) action() else Unit

    fun d(tag: String = DEFAULT_TAG, message: String) {
        ifDebug { scope.launch { Log.d(tag, message) } }
    }

    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        ifDebug { scope.launch { Log.e(tag, message, throwable) } }
    }

    fun w(tag: String = DEFAULT_TAG, message: String) {
        ifDebug { scope.launch { Log.w(tag, message) } }
    }

    fun i(tag: String = DEFAULT_TAG, message: String) {
        ifDebug { scope.launch { Log.i(tag, message) } }
    }

    fun v(tag: String = DEFAULT_TAG, message: String) {
        ifDebug { scope.launch { Log.v(tag, message) } }
    }
}
