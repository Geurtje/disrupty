package com.geuso.disrupty.ns.traveloption

data class TravelOption (
       var notification: TravelOptionNotification?,
       var numberOfTransfers:  Int,
       var optimal: Boolean,
       var disruptionStatus: DisruptionStatus
)



data class TravelOptionNotification(
        var severe: Boolean,
        var text: String
)


enum class DisruptionStatus(val key: String) {
    UNKNOWN("UNKOWN"),
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