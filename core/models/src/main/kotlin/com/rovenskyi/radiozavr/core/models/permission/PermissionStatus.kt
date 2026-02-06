package com.rovenskyi.radiozavr.core.models.permission

/**
 * Represents the status of a runtime permission.
 */
enum class PermissionStatus {
    /** Permission has not been requested yet */
    NOT_ASKED,

    /** Permission was granted by the user */
    GRANTED,

    /** Permission was denied by the user */
    DENIED,
}
