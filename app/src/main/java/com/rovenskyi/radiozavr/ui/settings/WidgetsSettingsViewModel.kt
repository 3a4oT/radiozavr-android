package com.rovenskyi.radiozavr.ui.settings

import androidx.lifecycle.ViewModel
import com.rovenskyi.radiozavr.domain.analytics.AnalyticsTracker
import com.rovenskyi.radiozavr.domain.analytics.event.ScreenEvent
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
