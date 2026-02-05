package com.rovenskyi.radiozavr.domain.analytics.event

sealed interface AnalyticsEvent {
    val name: String
    val params: Map<String, Any>
}
