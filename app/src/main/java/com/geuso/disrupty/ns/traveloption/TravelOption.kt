package com.geuso.disrupty.ns.traveloption

data class TravelOption (
       var notification: TravelOptionNotification?,
       var numberOfTransfers:  Int,
       var optimal: Boolean,
       var status: Status
//       var plannedTravelTime: String,
//       var actualTravelTime: String,
)



data class TravelOptionNotification(
        var severe: Boolean,
        var text: String
)


enum class Status(val key: String) {
    UNKNOWN("UNKOWN"),
    ACCORDING_TO_PLAN("VOLGENS-PLAN"),
    CHANGED("GEWIJZIGD"),
    DELAYED("VERTRAAGD"),
    NEW("NIEUW"),
    NOT_OPTIMAL("NIET-OPTIMAAL"),
    NOT_POSSIBLE("NIET-MOGELIJK"),
    PLAN_CHANGED("PLAN-GEWIJZIGD");

    companion object {
        val LOOKUP : Map<String, Status>

        init {
            LOOKUP = HashMap()
            for (status in Status.values()) {
                LOOKUP[status.key] = status
            }
        }
    }

}