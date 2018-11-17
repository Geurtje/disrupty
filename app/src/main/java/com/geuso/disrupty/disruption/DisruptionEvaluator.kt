package com.geuso.disrupty.disruption

import com.geuso.disrupty.ns.traveloption.DisruptionStatus
import com.geuso.disrupty.ns.traveloption.TravelOption

object DisruptionEvaluator {

    private val DISRUPTED_STATUSES : List<DisruptionStatus> = listOf(DisruptionStatus.DELAYED, DisruptionStatus.NOT_POSSIBLE)

    /**
     * A List of traveloptions is considered disrupted if either of the following is true:
     * - The status of any traveloption is "DELAYED" or "NOT_POSSIBLE"
     * - Any traveloption has a notification that is marked as severe
     */
    fun getDisruptionCheckResultFromTravelOptions(travelOptions: List<TravelOption>) : DisruptionCheckResult {
        for (travelOption in travelOptions) {
            if (DISRUPTED_STATUSES.contains(travelOption.disruptionStatus)
                    ||    hasSevereNotification(travelOption)
                    &&  travelOption.disruptionStatus != DisruptionStatus.ACCORDING_TO_PLAN
            ) {
                return DisruptionCheckResult(true, travelOption.notification?.text, travelOption.disruptionStatus)
            }
        }
        return DisruptionCheckResult(false, null, DisruptionStatus.ACCORDING_TO_PLAN)
    }

    private fun hasSevereNotification(travelOption: TravelOption) : Boolean =
            travelOption.notification != null && travelOption.notification!!.severe


    /**
     * DisruptionCheckResult groups facts related to the result of a disruption check.
     *
     * @param isDisrupted A Boolean indicating if the subscription is considered disrupted or not.
     * @param message A message from the TravelOption giving details about why this route is disrupted.
     *          Only present if a disruption is found.
     * @param disruptionStatus The DisruptionStatus that was used to determine if there is a disruption or not.
     */
    data class DisruptionCheckResult(val isDisrupted: Boolean, val message: String?, val disruptionStatus: DisruptionStatus)


}