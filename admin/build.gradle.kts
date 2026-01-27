plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.rovenskyi.radiolux.admin"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.rovenskyi.radiolux.admin"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:models"))
    implementation(project(":core:ui-theme"))
    implementation(project(":core:ui-components"))
    implementation(project(":core:config"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.material3)
    implementation(libs.androidx.navigation.compose)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
}
