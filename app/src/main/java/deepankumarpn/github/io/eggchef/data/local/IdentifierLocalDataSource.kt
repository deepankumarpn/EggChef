package deepankumarpn.github.io.eggchef.data.local

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.installations.FirebaseInstallations
import dagger.hilt.android.qualifiers.ApplicationContext
import deepankumarpn.github.io.eggchef.domain.model.AppIdentifiers
import deepankumarpn.github.io.eggchef.utils.PrefKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IdentifierLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStorePreference: DataStorePreference,
    private val dispatcher: CoroutineDispatcher
) {

    // ============================================
    // IDENTITY IDs
    // ============================================

    suspend fun getAdvertisingId(): String? = withContext(dispatcher) {
        try {
            val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
            if (adInfo.isLimitAdTrackingEnabled) "limited" else adInfo.id
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getInstallationId(): String? = try {
        FirebaseInstallations.getInstance().id.await()
    } catch (e: Exception) {
        null
    }

    fun getAndroidId(): String = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: "unknown"

    suspend fun getLaunchSessionId(): String {
        return dataStorePreference.getString(PrefKeys.KEY_LAUNCH_SESSION_ID)
            ?: initializeNewLaunchSession()
    }

    suspend fun initializeNewLaunchSession(): String {
        val sessionId = UUID.randomUUID().toString()
        dataStorePreference.putString(PrefKeys.KEY_LAUNCH_SESSION_ID, sessionId)
        return sessionId
    }

    // ============================================
    // APP INFO
    // ============================================

    fun getAppVersionName(): String = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "unknown"
    } catch (e: Exception) {
        "unknown"
    }

    fun getAppVersionCode(): Long = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toLong()
        }
    } catch (e: Exception) {
        0L
    }

    // ============================================
    // DEVICE INFO
    // ============================================

    fun getDeviceBrand(): String = Build.BRAND ?: "unknown"

    fun getDeviceModel(): String = Build.MODEL ?: "unknown"

    fun getDeviceManufacturer(): String = Build.MANUFACTURER ?: "unknown"

    fun getOsVersion(): String = Build.VERSION.RELEASE ?: "unknown"

    fun getOsApiLevel(): Int = Build.VERSION.SDK_INT

    // ============================================
    // LOCATION INFO
    // ============================================

    fun getCountryCode(): String = try {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        val simCountry = tm?.simCountryIso?.uppercase()
        if (!simCountry.isNullOrEmpty()) simCountry
        else Locale.getDefault().country.uppercase().ifEmpty { "UNKNOWN" }
    } catch (e: Exception) {
        Locale.getDefault().country.uppercase().ifEmpty { "UNKNOWN" }
    }

    fun getLanguageCode(): String = Locale.getDefault().language.uppercase()

    // ============================================
    // AGGREGATE
    // ============================================

    suspend fun getAllIdentifiers(): AppIdentifiers = AppIdentifiers(
        advertisingId = getAdvertisingId(),
        installationId = getInstallationId(),
        androidId = getAndroidId(),
        launchSessionId = getLaunchSessionId(),
        appVersionName = getAppVersionName(),
        appVersionCode = getAppVersionCode(),
        deviceBrand = getDeviceBrand(),
        deviceModel = getDeviceModel(),
        deviceManufacturer = getDeviceManufacturer(),
        osVersion = getOsVersion(),
        osApiLevel = getOsApiLevel(),
        countryCode = getCountryCode(),
        languageCode = getLanguageCode()
    )
}
