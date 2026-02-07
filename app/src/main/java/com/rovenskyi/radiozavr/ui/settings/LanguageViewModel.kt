package com.rovenskyi.radiozavr.ui.settings

import androidx.lifecycle.ViewModel
import com.rovenskyi.radiozavr.core.models.language.LanguageMode
import com.rovenskyi.radiozavr.domain.analytics.AnalyticsTracker
import com.rovenskyi.radiozavr.domain.analytics.event.ScreenEvent
import com.rovenskyi.radiozavr.domain.analytics.event.SettingsEvent
import com.rovenskyi.radiozavr.domain.language.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val languageRepository: LanguageRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    private val _selectedMode = MutableStateFlow(languageRepository.getLanguageMode())
    val selectedMode: StateFlow<LanguageMode> = _selectedMode.asStateFlow()

    init {
        analyticsTracker.track(ScreenEvent.Language)
    }

    fun setLanguageMode(mode: LanguageMode) {
        _selectedMode.value = mode
        languageRepository.setLanguageMode(mode)
        analyticsTracker.track(SettingsEvent.LanguageChanged(mode))
    }
}
