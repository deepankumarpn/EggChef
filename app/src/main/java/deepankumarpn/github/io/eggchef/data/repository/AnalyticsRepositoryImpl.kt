package deepankumarpn.github.io.eggchef.data.repository

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import deepankumarpn.github.io.eggchef.domain.model.AppIdentifiers
import deepankumarpn.github.io.eggchef.domain.repository.AnalyticsRepository
import deepankumarpn.github.io.eggchef.domain.repository.IdentifierRepository
import deepankumarpn.github.io.eggchef.utils.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val identifierRepository: IdentifierRepository,
    private val dispatcher: CoroutineDispatcher
) : AnalyticsRepository {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    @Volatile
    private var cachedIdentifiers: AppIdentifiers? = null
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun initialize() {
        scope.launch {
            try {
                cachedIdentifiers = identifierRepository.getIdentifiers()
            } catch (_: Exception) {
                // Identifiers unavailable; events will be sent without them
            }
        }
    }

    override fun sendEvent(eventName: String, params: Bundle) {
        val enrichedParams = Bundle(params).apply {
            cachedIdentifiers?.let { ids ->
                ids.toMap().forEach { (key, value) ->
                    when (value) {
                        is String -> putString(key, value)
                        is Long -> putLong(key, value)
                        is Int -> putInt(key, value)
                        null -> putString(key, "null")
                    }
                }
            }
        }
        logEvent(eventName, enrichedParams)
    }

    override fun logEvent(eventName: String, params: Bundle) {
        Logger.d(TAG, "logEvent: eventName=$eventName, params={${params.keySet().joinToString { "$it=${params[it]}" }}}")
        firebaseAnalytics.logEvent(eventName, params)
    }

    companion object {
        private const val TAG = "AnalyticsLog"
    }
}
