package com.rovenskyi.radiozavr.domain.analytics

import com.rovenskyi.radiozavr.domain.analytics.event.AnalyticsEvent

interface AnalyticsTracker {
    fun track(event: AnalyticsEvent)
    fun setUserProperty(name: String, value: String)
    fun logError(throwable: Throwable, context: Map<String, String> = emptyMap())
    fun setErrorContext(key: String, value: String)
}
