package com.rovenskyi.radiozavr.ui.settings

import androidx.lifecycle.ViewModel
import com.rovenskyi.radiozavr.core.models.language.LanguageMode
import com.rovenskyi.radiozavr.domain.analytics.AnalyticsTracker
import com.rovenskyi.radiozavr.domain.analytics.event.ScreenEvent
import com.rovenskyi.radiozavr.domain.language.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    languageRepository: LanguageRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    val languageMode: LanguageMode = languageRepository.getLanguageMode()

    init {
        analyticsTracker.track(ScreenEvent.Settings)
    }
}
