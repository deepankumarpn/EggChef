package deepankumarpn.github.io.eggchef.domain.repository

import android.os.Bundle

interface AnalyticsRepository {
    fun initialize()
    fun sendEvent(eventName: String, params: Bundle)
    fun logEvent(eventName: String, params: Bundle)
}
