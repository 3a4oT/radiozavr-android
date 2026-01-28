package com.rovenskyi.radiolux.core.models.language

/**
 * Available language modes for the app.
 * SYSTEM follows device language settings.
 */
enum class LanguageMode(val localeTag: String?) {
    SYSTEM(null),
    UKRAINIAN("uk"),
    ENGLISH("en")
}
