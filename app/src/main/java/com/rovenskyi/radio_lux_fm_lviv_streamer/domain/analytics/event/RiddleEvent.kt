package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.event

sealed class RiddleEvent : AnalyticsEvent {

    data class AnswerRevealed(val isTv: Boolean) : RiddleEvent() {
        override val name: String = "riddle_answer_revealed"
        override val params: Map<String, Any> = mapOf(PARAM_IS_TV to isTv)
    }

    companion object {
        const val PARAM_IS_TV = "is_tv"
    }
}
