package com.rovenskyi.radiozavr.core.models.riddle

/**
 * Available intervals for riddle rotation in seconds.
 */
enum class RiddleInterval(val seconds: Int) {
    SECONDS_6(6),
    SECONDS_12(12),
    SECONDS_20(20),
    SECONDS_30(30),
    SECONDS_60(60),
    ;

    val milliseconds: Long get() = seconds * 1000L

    /**
     * Seconds before end when timer turns red (warning state).
     * ~20% of interval, minimum 3 seconds.
     */
    val warningSeconds: Int get() = maxOf(3, (seconds * 0.2).toInt())

    /**
     * Seconds before end when answer auto-reveals (AUTOMATIC mode).
     * ~17% of interval, minimum 3 seconds.
     */
    val autoRevealSeconds: Int get() = maxOf(3, (seconds * 0.17).toInt())

    companion object {
        val DEFAULT = SECONDS_30

        fun fromSeconds(seconds: Int): RiddleInterval =
            entries.find { it.seconds == seconds } ?: DEFAULT
    }
}
