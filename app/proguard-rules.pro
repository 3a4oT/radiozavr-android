# Radiozavr ProGuard Rules
# Last updated: 2026-02-05
#
# Note: Many libraries include their own consumer-rules.pro:
# - kotlinx.coroutines 1.6+ (bundled rules)
# - Media3 1.0+ (bundled rules)
# - Hilt 2.41+ (bundled rules via plugin)
# Only app-specific and essential rules are kept here.

# ============================================
# Firebase Crashlytics - readable stack traces
# ============================================
# Required for deobfuscated crash reports
# See: https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ============================================
# Kotlin Serialization
# ============================================
# Required for @Serializable classes (Navigation routes, data classes)
# See: https://github.com/Kotlin/kotlinx.serialization#android
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}

-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.rovenskyi.**$$serializer { *; }

-keepclassmembers class com.rovenskyi.** {
    *** Companion;
}

# ============================================
# Media3 / ExoPlayer
# ============================================
# Suppress warnings for unused formats (app uses HLS only)
-dontwarn androidx.media3.datasource.rtmp.**
-dontwarn androidx.media3.exoplayer.dash.**
-dontwarn androidx.media3.exoplayer.smoothstreaming.**

# Keep Player.Listener implementations (reflection callbacks)
-keep interface androidx.media3.common.Player$Listener { *; }
-keep class * implements androidx.media3.common.Player$Listener { *; }

# Keep MediaSessionService subclasses
-keep class * extends androidx.media3.session.MediaSessionService { *; }

# ============================================
# Hilt / Dagger
# ============================================
# Hilt 2.41+ includes consumer rules, but keep this for safety
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# ============================================
# App-specific
# ============================================
# Keep PlayerEventReceiver (singleton for service-UI communication via Flow)
-keep class com.rovenskyi.radiozavr.service.PlayerEventReceiver { *; }

# Keep RadioService companion object (intent action constants)
-keep class com.rovenskyi.radiozavr.service.RadioService$Companion { *; }

# ============================================
# Debug
# ============================================
# Uncomment to output full R8 configuration for debugging:
# -printconfiguration build/outputs/logs/r8-configuration.txt
