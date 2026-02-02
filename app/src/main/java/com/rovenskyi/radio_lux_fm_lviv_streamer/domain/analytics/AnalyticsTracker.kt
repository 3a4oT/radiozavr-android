package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics

import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.event.AnalyticsEvent

interface AnalyticsTracker {
    fun track(event: AnalyticsEvent)
    fun setUserProperty(name: String, value: String)
    fun logError(throwable: Throwable, context: Map<String, String> = emptyMap())
    fun setErrorContext(key: String, value: String)
}
