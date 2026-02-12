package deepankumarpn.github.io.eggchef.domain.model

data class AppIdentifiers(
    // Identity IDs
    val advertisingId: String?,
    val installationId: String?,
    val androidId: String,
    val launchSessionId: String,

    // App Info
    val appVersionName: String,
    val appVersionCode: Long,

    // Device Info
    val deviceBrand: String,
    val deviceModel: String,
    val deviceManufacturer: String,
    val osVersion: String,
    val osApiLevel: Int,

    // Location Info
    val countryCode: String,
    val languageCode: String,

) {
    fun toMap(): Map<String, Any?> = mapOf(
        "advertising_id" to advertisingId,
        "installation_id" to installationId,
        "android_id" to androidId,
        "launch_session_id" to launchSessionId,
        "app_version_name" to appVersionName,
        "app_version_code" to appVersionCode,
        "device_brand" to deviceBrand,
        "device_model" to deviceModel,
        "device_manufacturer" to deviceManufacturer,
        "os_version" to osVersion,
        "os_api_level" to osApiLevel,
        "country_code" to countryCode,
        "language_code" to languageCode,
        "timestamp" to System.currentTimeMillis()
    )
}
