package com.rovenskyi.radiozavr.domain.analytics.event

import com.rovenskyi.radiozavr.domain.analytics.model.ButtonName
import com.rovenskyi.radiozavr.domain.analytics.model.ScreenName

data class ButtonEvent(
    val buttonName: ButtonName,
    val screenName: ScreenName,
) : AnalyticsEvent {

    override val name: String = "button_click"

    override val params: Map<String, Any> = mapOf(
        PARAM_BUTTON_NAME to buttonName.value,
        PARAM_SCREEN_NAME to screenName.value,
    )

    companion object {
        const val PARAM_BUTTON_NAME = "button_name"
        const val PARAM_SCREEN_NAME = "screen_name"
    }
}
