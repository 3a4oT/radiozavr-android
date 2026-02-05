package com.rovenskyi.radiozavr.domain.analytics.model

enum class StopReason(val value: String) {
    USER_CLICK("user_click"),
    ERROR("error"),
    AUTO_STOP("auto_stop"),
}
