package com.geuso.disrupty.ns.traveloption

import java.time.Instant
import java.util.*

data class TravelOption (
        val notification: TravelOptionNotification?,
        val numberOfTransfers:  Int,
        val optimal: Boolean,
        val disruptionStatus: DisruptionStatus,
        val plannedDepartureTime: Instant? = null,
        val currentDepartureTime: Instant? = null,
        val departureDelay: String? = null
)



data class TravelOptionNotification(
        var severe: Boolean,
        var text: String
)


enum class DisruptionStatus(val key: String) {
    UNKNOWN("UNKOWN"),

    // Status codes from the public travel trips API
    CANCELLED("CANCELLED"),
    CHANGE_NOT_POSSIBLE("CHANGE_NOT_POSSIBLE"),
    CHANGE_COULD_BE_POSSIBLE("CHANGE_COULD_BE_POSSIBLE"),
    ALTERNATIVE_TRANSPORT("ALTERNATIVE_TRANSPORT"),
    DISRUPTION("DISRUPTION"),
    MAINTENANCE("MAINTENANCE"),
    REPLACEMENT("REPLACEMENT"),
    ADDITIONAL("ADDITIONAL"),
    SPECIAL("SPECIAL"),
    NORMAL("NORMAL"),

    // Status codes from the deprecated trips API
    ACCORDING_TO_PLAN("VOLGENS-PLAN"),
    CHANGED("GEWIJZIGD"),
    DELAYED("VERTRAAGD"),
    NEW("NIEUW"),
    NOT_OPTIMAL("NIET-OPTIMAAL"),
    NOT_POSSIBLE("NIET-MOGELIJK"),
    PLAN_CHANGED("PLAN-GEWIJZIGD");

    companion object {
        val LOOKUP : Map<String, DisruptionStatus>

        init {
            LOOKUP = HashMap()
            for (status in DisruptionStatus.values()) {
                LOOKUP[status.key] = status
            }
        }
    }
}