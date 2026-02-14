import java.io.FileInputStream
import java.util.Properties
import kotlin.toString

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
}

android {
    namespace = "deepankumarpn.github.io.eggchef"
    compileSdk = 36

    defaultConfig {
        applicationId = "deepankumarpn.github.io.eggchef"
        minSdk = 28
        targetSdk = 36
        val (verCode, verName) = genVersion()
        versionCode = verCode
        versionName = verName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val localProperties = Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) load(file.inputStream())
    }

    signingConfigs {
        create("release") {
            storeFile = file(localProperties.getProperty("RELEASE_STORE_FILE", ""))
            storePassword = localProperties.getProperty("RELEASE_STORE_PASSWORD", "")
            keyAlias = localProperties.getProperty("RELEASE_KEY_ALIAS", "")
            keyPassword = localProperties.getProperty("RELEASE_KEY_PASSWORD", "")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    val (_, verName) = genVersion()
    val buildNum = Properties().apply {
        load(FileInputStream(file("../versions.properties")))
    }["build"].toString().trim()

    applicationVariants.all {
        val appName = "eggchef_${verName}_${buildType.name}_RC${buildNum}"
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output.outputFileName = "$appName.apk"
        }
    }
}

tasks.whenTaskAdded {
    if (name.startsWith("bundle")) {
        val match = Regex("bundle(Debug|Release)").find(name) ?: return@whenTaskAdded
        val buildType = match.groupValues[1].lowercase()
        val (_, verName) = genVersion()
        val buildNum = Properties().apply {
            load(FileInputStream(file("../versions.properties")))
        }["build"].toString().trim()
        val appName = "eggchef_${verName}_${buildType}_RC${buildNum}"

        doLast {
            val aabDir = layout.buildDirectory.dir("outputs/bundle/$buildType").get().asFile
            aabDir.listFiles()?.filter { it.extension == "aab" }?.forEach { file ->
                file.renameTo(File(aabDir, "$appName.aab"))
            }
        }
    }
}

dependencies {
    implementation(project(":base"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    // Firebase
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.installations)

    // Play Services
    implementation(libs.play.services.ads.identifier)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Gson
    implementation(libs.gson)

    // Security
    implementation(libs.androidx.security.crypto)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

fun genVersion(): Pair<Int, String> {
    val versionPro = Properties()
    versionPro.load(FileInputStream(file("../versions.properties")))
    val major = versionPro["major"].toString().trim().toInt()
    val minor = versionPro["minor"].toString().trim().toInt()
    val patch = versionPro["patch"].toString().trim().toInt()
    val build = versionPro["build"].toString().trim().toInt()
    val verCode = (major * 1_000_000) + (minor * 10_000) + (patch * 100) + build
    val verName = "$major.$minor.$patch"
    return Pair(verCode, verName)
}
