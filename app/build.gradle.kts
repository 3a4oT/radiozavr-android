import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics.plugin)
    id("com.google.dagger.hilt.android")
}

val signingProps = Properties().apply {
    val localProps = rootProject.file("local.properties")
    if (localProps.exists()) load(localProps.inputStream())
}

android {
    namespace = "com.rovenskyi.radiozavr"
    compileSdk = 36

    signingConfigs {
        create("release") {
            storeFile = rootProject.file(
                signingProps.getProperty("signing.storeFile", "signing/release-key.jks")
            )
            storePassword = signingProps.getProperty("signing.storePassword")
                ?: System.getenv("KEYSTORE_PASSWORD")
            keyAlias = signingProps.getProperty("signing.keyAlias")
                ?: System.getenv("KEY_ALIAS")
                ?: "release-key-alias"
            keyPassword = signingProps.getProperty("signing.keyPassword")
                ?: System.getenv("KEY_PASSWORD")
        }
    }

    defaultConfig {
        applicationId = "com.rovenskyi.radiozavr"
        minSdk = 26
        targetSdk = 36
        // Fallbacks for local builds only; releases get both from CI (versionName from the tag,
        // versionCode from the build date - see .github/workflows/release.yml).
        versionCode = (findProperty("versionCode") as String?)?.toInt() ?: 26082916
        versionName = findProperty("versionName") as String? ?: "0.9.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "STREAM_URL",
            "\"http://streamvideo.luxnet.ua/luxlviv/luxlviv.stream/chunklist.m3u8\""
        )
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    // Disable language splitting for App Bundle
    // Required because we support in-app language switching
    bundle {
        language {
            enableSplit = false
        }
    }

}

dependencies {
    // Core modules
    implementation(project(":core:models"))
    implementation(project(":core:widget-protocol"))
    implementation(project(":core:ui-theme"))
    implementation(project(":core:ui-components"))
    implementation(project(":core:network"))

    // Firebase BOM - manages versions for all Firebase libraries
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Compose BOM - manages versions for all Compose libraries
    implementation(platform(libs.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)

    // For media playback using ExoPlayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    // Dependency injection
    implementation(libs.hilt.android)
    implementation(libs.androidx.media3.session)
    ksp(libs.hilt.android.compiler)

    // Debug
    debugImplementation(libs.androidx.ui.tooling)

    // Test
    testImplementation(libs.junit)
    testRuntimeOnly(libs.junit.platform.launcher)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
