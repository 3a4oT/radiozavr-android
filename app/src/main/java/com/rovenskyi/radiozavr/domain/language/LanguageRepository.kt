package com.rovenskyi.radiozavr.domain.language

import com.rovenskyi.radiozavr.core.models.language.LanguageMode

/**
 * Repository for managing app language settings.
 * Uses AndroidX AppCompat per-app language API.
 */
interface LanguageRepository {
    /**
     * Get current language mode.
     */
    fun getLanguageMode(): LanguageMode

    /**
     * Set app language mode.
     * This will trigger Activity recreation.
     */
    fun setLanguageMode(mode: LanguageMode)
}
