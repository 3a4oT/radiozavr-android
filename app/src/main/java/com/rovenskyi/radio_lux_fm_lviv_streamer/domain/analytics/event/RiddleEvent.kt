package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.event

sealed class RiddleEvent : AnalyticsEvent {

    data class AnswerRevealed(
        val isTv: Boolean,
        val isAutomatic: Boolean,
    ) : RiddleEvent() {
        override val name: String = "riddle_answer_revealed"
        override val params: Map<String, Any> = mapOf(
            PARAM_IS_TV to isTv,
            PARAM_IS_AUTOMATIC to isAutomatic,
        )
    }

    companion object {
        const val PARAM_IS_TV = "is_tv"
        const val PARAM_IS_AUTOMATIC = "is_automatic"
    }
}
