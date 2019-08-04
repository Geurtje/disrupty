package com.geuso.disrupty.disruption

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.geuso.disrupty.R
import com.geuso.disrupty.ns.traveloption.DisruptionStatus
import com.geuso.disrupty.ns.traveloption.TravelOption

/*
* TODO remove the SharedPreferences dependency. The reason it's present because I wasn't able to
* mock return a mocked instance from PreferenceManager.getDefaultSharedPreferences(context).
* Thus, the only reason its present is for unit testing. */
class DisruptionEvaluator(
        context: Context,
        sharedPreferences: SharedPreferences
) {

    private val disruptedStatuses : List<DisruptionStatus> = listOf(
            DisruptionStatus.NOT_POSSIBLE,
            DisruptionStatus.CANCELLED,
            DisruptionStatus.DELAYED,
            DisruptionStatus.DISRUPTION
    )
    private val minimumDelayInMs: Int

    init {
        // For whatever reason sharedPreferences.getInt throws a ClassCastException,
        // so we need to get the String value and manually convert it...
        val minimumDelayInMinutes = Integer.parseInt(sharedPreferences.getString("pref_disruption_minimum_delay_time",
                context.resources.getInteger(R.integer.default_delay_disruption_window_in_minutes).toString()))

        minimumDelayInMs = minimumDelayInMinutes * 60_000 // 1 minute = 60.000 ms
    }


    /**
     * A List of traveloptions is considered disrupted if either of the following is true:
     * - The status of any traveloption is "DELAYED" or "NOT_POSSIBLE"
     * - Any traveloption has a notification that is marked as severe
     * - Any traveloption has a current departure time that is 2 minutes higher than the planned departure time
     */
    fun getDisruptionCheckResultFromTravelOptions(travelOptions: List<TravelOption>) : DisruptionCheckResult {
        for (travelOption in travelOptions) {
            if (travelOption.disruptionStatus == DisruptionStatus.ACCORDING_TO_PLAN) {
                continue
            }

            if (disruptedStatuses.contains(travelOption.disruptionStatus)
                    || hasSevereNotification(travelOption)
                    || hasSignificantDepartureDelay(travelOption)
            ) {
                return DisruptionCheckResult(true,
                        travelOption.notification?.text,
                        travelOption.disruptionStatus,
                        travelOption.departureDelay
                )
            }
        }
        return DisruptionCheckResult(false, null, DisruptionStatus.ACCORDING_TO_PLAN)
    }

    private fun hasSevereNotification(travelOption: TravelOption) : Boolean =
            travelOption.notification != null && travelOption.notification.severe


    private fun hasSignificantDepartureDelay(travelOption: TravelOption): Boolean {
        val planned = travelOption.plannedDepartureTime
        val current = travelOption.currentDepartureTime

        if (planned != null && current != null) {
            val minusSeconds = current.minusSeconds(planned.epochSecond)
            if (minusSeconds.toEpochMilli() > minimumDelayInMs) {
                return true
            }
        }

        return false
    }

    /**
     * DisruptionCheckResult groups facts related to the result of a disruption check.
     *
     * @param isDisrupted A Boolean indicating if the subscription is considered disrupted or not.
     * @param message A message from the TravelOption giving details about why this route is disrupted.
     *          Only present if a disruption is found.
     * @param disruptionStatus The DisruptionStatus that was used to determine if there is a disruption or not.
     */
    data class DisruptionCheckResult(
            val isDisrupted: Boolean,
            val message: String?,
            val disruptionStatus: DisruptionStatus,
            val departureDelay: String? = null
    )

}