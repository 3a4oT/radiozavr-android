package com.rovenskyi.radiozavr.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radiozavr.core.models.theme.ThemeMode
import com.rovenskyi.radiozavr.domain.analytics.AnalyticsTracker
import com.rovenskyi.radiozavr.domain.analytics.event.ScreenEvent
import com.rovenskyi.radiozavr.domain.analytics.event.SettingsEvent
import com.rovenskyi.radiozavr.domain.theme.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    private val _isThemeLoaded = MutableStateFlow(false)
    val isThemeLoaded: StateFlow<Boolean> = _isThemeLoaded.asStateFlow()

    // Using Eagerly to start collecting immediately (needed for splash screen)
    // Theme loading must happen before UI renders to avoid theme flash
    val themeMode: StateFlow<ThemeMode> = themeRepository.themeMode
        .onEach { _isThemeLoaded.value = true }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ThemeMode.DARK,
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themeRepository.setThemeMode(mode)
            analyticsTracker.track(SettingsEvent.ThemeChanged(mode))
        }
    }

    fun trackThemeScreenView() {
        analyticsTracker.track(ScreenEvent.Theme)
    }
}
