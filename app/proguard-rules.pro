# Radiozavr ProGuard Rules

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
# Media3 / ExoPlayer
# ============================================
# Suppress warnings for unused formats (HLS only)
-dontwarn androidx.media3.datasource.rtmp.**
-dontwarn androidx.media3.exoplayer.dash.**
-dontwarn androidx.media3.exoplayer.smoothstreaming.**

# Keep Player.Listener interface and implementations
-keep interface androidx.media3.common.Player$Listener { *; }
-keep class * implements androidx.media3.common.Player$Listener { *; }

# Keep MediaSessionService and MediaSession
-keep class * extends androidx.media3.session.MediaSessionService { *; }
-keep class * extends androidx.media3.session.MediaSession$Callback { *; }
-keep class androidx.media3.session.** { *; }

# ============================================
# Hilt / Dagger - keep injection points
# ============================================
-keep class dagger.hilt.android.internal.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }

# Keep @Inject annotated fields and constructors
-keepclasseswithmembers class * {
    @javax.inject.Inject <fields>;
}
-keepclasseswithmembers class * {
    @javax.inject.Inject <init>(...);
}

# Keep @AndroidEntryPoint classes
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# ============================================
# App-specific - keep service communication
# ============================================
# Keep PlayerEventReceiver (singleton for service-UI communication)
-keep class com.rovenskyi.radiozavr.service.PlayerEventReceiver { *; }

# Keep RadioService companion object (intent actions)
-keep class com.rovenskyi.radiozavr.service.RadioService$Companion { *; }

# ============================================
# Debug - configuration output
# ============================================
# Uncomment to debug R8 rules:
# -printconfiguration build/outputs/logs/r8-configuration.txt
