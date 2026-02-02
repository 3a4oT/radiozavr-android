package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.event

import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.model.ErrorType
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.model.PlaySource
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.model.StopReason

sealed class PlayerEvent : AnalyticsEvent {

    data class PlayStarted(val source: PlaySource) : PlayerEvent() {
        override val name: String = "play_started"
        override val params: Map<String, Any> = mapOf(PARAM_SOURCE to source.value)
    }

    data class PlayStopped(
        val reason: StopReason,
        val durationMs: Long = 0,
    ) : PlayerEvent() {
        override val name: String = "play_stopped"
        override val params: Map<String, Any> = mapOf(
            PARAM_REASON to reason.value,
            PARAM_DURATION_MS to durationMs,
        )
    }

    data class PlaybackError(
        val errorType: ErrorType,
        val errorCode: Int? = null,
        val errorMessage: String? = null,
    ) : PlayerEvent() {
        override val name: String = "playback_error"
        override val params: Map<String, Any> = buildMap {
            put(PARAM_ERROR_TYPE, errorType.value)
            errorCode?.let { put(PARAM_ERROR_CODE, it) }
            errorMessage?.let { put(PARAM_ERROR_MESSAGE, it) }
        }
    }

    data class BufferingStarted(val isRebuffer: Boolean) : PlayerEvent() {
        override val name: String = "buffering_started"
        override val params: Map<String, Any> = mapOf(PARAM_IS_REBUFFER to isRebuffer)
    }

    data class NetworkError(val wasPlaying: Boolean) : PlayerEvent() {
        override val name: String = "network_error"
        override val params: Map<String, Any> = mapOf(PARAM_WAS_PLAYING to wasPlaying)
    }

    companion object {
        const val PARAM_SOURCE = "source"
        const val PARAM_REASON = "reason"
        const val PARAM_DURATION_MS = "duration_ms"
        const val PARAM_ERROR_TYPE = "error_type"
        const val PARAM_ERROR_CODE = "error_code"
        const val PARAM_ERROR_MESSAGE = "error_message"
        const val PARAM_IS_REBUFFER = "is_rebuffer"
        const val PARAM_WAS_PLAYING = "was_playing"
    }
}
