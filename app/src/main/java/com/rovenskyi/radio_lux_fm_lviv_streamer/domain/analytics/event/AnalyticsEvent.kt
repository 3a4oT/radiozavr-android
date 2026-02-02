package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.event

sealed interface AnalyticsEvent {
    val name: String
    val params: Map<String, Any>
}
