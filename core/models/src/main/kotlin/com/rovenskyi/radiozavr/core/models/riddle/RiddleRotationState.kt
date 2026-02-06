package com.rovenskyi.radiozavr.core.models.riddle

/**
 * State of the riddle rotation cycle.
 *
 * @param riddle Current riddle being displayed
 * @param startTimeMillis Timestamp when current riddle started (for animation sync)
 * @param index Sequential index of the current riddle (for analytics/debugging)
 */
data class RiddleRotationState(
    val riddle: Riddle? = null,
    val startTimeMillis: Long = System.currentTimeMillis(),
    val index: Int = 0,
)
