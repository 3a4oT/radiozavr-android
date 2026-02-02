package com.rovenskyi.radio_lux_fm_lviv_streamer.data.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.AnalyticsTracker
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.event.AnalyticsEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsTracker @Inject constructor(
    @ApplicationContext context: Context,
) : AnalyticsTracker {

    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    override fun track(event: AnalyticsEvent) {
        val bundle = event.params.toBundle()
        firebaseAnalytics.logEvent(event.name, bundle)
    }

    override fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
        crashlytics.setCustomKey(name, value)
    }

    override fun logError(throwable: Throwable, context: Map<String, String>) {
        context.forEach { (key, value) ->
            crashlytics.setCustomKey(key, value)
        }
        crashlytics.recordException(throwable)
    }

    override fun setErrorContext(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    private fun Map<String, Any>.toBundle(): Bundle {
        return Bundle().apply {
            this@toBundle.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Float -> putFloat(key, value)
                    else -> putString(key, value.toString())
                }
            }
        }
    }
}
