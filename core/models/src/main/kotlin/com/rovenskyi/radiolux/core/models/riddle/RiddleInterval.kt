package com.rovenskyi.radiolux.core.models.riddle

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

    companion object {
        val DEFAULT = SECONDS_30

        fun fromSeconds(seconds: Int): RiddleInterval =
            entries.find { it.seconds == seconds } ?: DEFAULT
    }
}
