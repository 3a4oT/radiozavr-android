# Radio Lux FM ProGuard Rules

# ============================================
# Firebase Crashlytics - readable stack traces
# ============================================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ============================================
# Kotlin Serialization
# ============================================
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
# Kotlin Coroutines
# ============================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# ============================================
# Media3 / ExoPlayer (HLS only - reduce size)
# ============================================
-dontwarn androidx.media3.datasource.rtmp.**
-dontwarn androidx.media3.exoplayer.dash.**
-dontwarn androidx.media3.exoplayer.smoothstreaming.**

# ============================================
# Debug - configuration output
# ============================================
# Uncomment to debug R8 rules:
# -printconfiguration build/outputs/logs/r8-configuration.txt
