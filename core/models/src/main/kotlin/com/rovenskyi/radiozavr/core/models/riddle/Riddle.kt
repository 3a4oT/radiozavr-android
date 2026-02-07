package com.rovenskyi.radiozavr.core.models.riddle

import kotlinx.serialization.Serializable

/**
 * A single riddle with question, answer, and display emoji.
 */
@Serializable
data class Riddle(
    val question: String,
    val answer: String,
    val emoji: String = "🧩",
)
