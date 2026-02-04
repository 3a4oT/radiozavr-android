plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.rovenskyi.radiolux.core.components"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
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

    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

    // QR Code generation
    implementation(libs.zxing.core)
}
