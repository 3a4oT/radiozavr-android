package com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel

import androidx.lifecycle.ViewModel
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.AnalyticsTracker
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.event.ScreenEvent
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.event.SettingsEvent
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.LanguageRepository
import com.rovenskyi.radiolux.core.models.language.LanguageMode
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
