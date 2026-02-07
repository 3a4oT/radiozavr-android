package com.rovenskyi.radiozavr.data.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.rovenskyi.radiozavr.core.models.language.LanguageMode
import com.rovenskyi.radiozavr.domain.language.LanguageRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageRepositoryImpl @Inject constructor() : LanguageRepository {

    override fun getLanguageMode(): LanguageMode {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales.isEmpty) {
            return LanguageMode.SYSTEM
        }
        val firstLocale = locales[0]
        return when (firstLocale?.language) {
            "uk" -> LanguageMode.UKRAINIAN
            "en" -> LanguageMode.ENGLISH
            else -> LanguageMode.SYSTEM
        }
    }

    override fun setLanguageMode(mode: LanguageMode) {
        val localeList = if (mode.localeTag != null) {
            LocaleListCompat.forLanguageTags(mode.localeTag)
        } else {
            LocaleListCompat.getEmptyLocaleList()
        }
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
