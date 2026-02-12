package deepankumarpn.github.io.eggchef.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import deepankumarpn.github.io.eggchef.utils.PrefKeys
import deepankumarpn.github.io.eggchef.utils.SecurityUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PrefKeys.PREF_NAME)

@Singleton
class DataStorePreference @Inject constructor(
    private val context: Context,
    private val securityUtil: SecurityUtil,
    @PublishedApi internal val gson: Gson
) {

    private val dataStore get() = context.dataStore

    suspend fun putString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        val encrypted = securityUtil.encrypt(value)
        dataStore.edit { prefs ->
            prefs[prefKey] = encrypted
        }
    }

    suspend fun getString(key: String, defaultValue: String? = null): String? {
        val prefKey = stringPreferencesKey(key)
        val encrypted = dataStore.data.map { prefs -> prefs[prefKey] }.first()
        return if (encrypted != null) securityUtil.decrypt(encrypted) else defaultValue
    }

    suspend fun <T> putObject(key: String, value: T) {
        val json = gson.toJson(value)
        putString(key, json)
    }

    suspend inline fun <reified T> getObject(key: String): T? {
        val json = getString(key) ?: return null
        return try {
            gson.fromJson(json, object : TypeToken<T>() {}.type)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun <T> putList(key: String, list: List<T>) {
        val json = gson.toJson(list)
        putString(key, json)
    }

    suspend inline fun <reified T> getList(key: String): List<T> {
        val json = getString(key) ?: return emptyList()
        return try {
            gson.fromJson(json, object : TypeToken<List<T>>() {}.type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun remove(key: String) {
        val prefKey = stringPreferencesKey(key)
        dataStore.edit { prefs ->
            prefs.remove(prefKey)
        }
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
