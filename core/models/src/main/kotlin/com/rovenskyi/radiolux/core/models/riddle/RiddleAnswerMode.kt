package com.rovenskyi.radiolux.core.models.riddle

/**
 * Mode for revealing riddle answers.
 */
enum class RiddleAnswerMode {
    /**
     * Answer is revealed when user clicks/presses OK.
     */
    MANUAL,

    /**
     * Answer is revealed automatically before timer ends.
     * User can still reveal manually by clicking earlier.
     */
    AUTOMATIC,
    ;

    companion object {
        val DEFAULT = AUTOMATIC
    }
}
