package com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel

import androidx.lifecycle.ViewModel
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.AnalyticsTracker
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.event.ScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WidgetsSettingsViewModel @Inject constructor(
    analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    init {
        analyticsTracker.track(ScreenEvent.Widgets)
    }
}
