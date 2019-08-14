package com.geuso.disrupty.ns.traveloption

import android.annotation.SuppressLint
import android.util.Log
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant

class TravelOptionJsonParser {

    companion object {
        private val TAG = TravelOptionJsonParser::class.qualifiedName
        private const val DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ"

        private const val TRIPS = "trips"

        private const val TRANSFERS = "transfers"
        private const val OPTIMAL = "optimal"
        private const val STATUS = "status"

        private const val LEGS = "legs"
        private const val MESSAGES = "messages"
        private const val MESSAGE_TEXT = "text"

        private const val ORIGIN = "origin"
        private const val PLANNED_DATE_TIME = "plannedDateTime"
        private const val ACTUAL_DATE_TIME = "actualDateTime"
    }

    fun parseTravelOptions(json: JSONObject) : List<TravelOption> {
        val travelOptionsList = arrayListOf<TravelOption>()

        if (json.has(TRIPS)) {
            val travelOptionsJsonArray = json.getJSONArray(TRIPS)

            val numberOfTravelOptions = travelOptionsJsonArray.length()
            for (i in 0 until numberOfTravelOptions) {
                val station = parseTravelOption(travelOptionsJsonArray.getJSONObject(i))
                travelOptionsList.add(station)
            }
        }

        return travelOptionsList
    }

    private fun parseTravelOption(jsonTravelOption: JSONObject) : TravelOption {

        val numberOfTransfers = jsonTravelOption.getInt(TRANSFERS)
        val optimal = jsonTravelOption.getBoolean(OPTIMAL)
        val notification = parseNotificationMessage(jsonTravelOption)
        val (plannedDepartureTime, actualDepartureTime) = getPlannedAndCurrentDepartureTimes(jsonTravelOption)

        val delayInMinutes = getDelayInMinutes(plannedDepartureTime, actualDepartureTime)
        val delayString = formatDelayString(delayInMinutes)

        val status = parseStatus(jsonTravelOption, delayInMinutes)

        return TravelOption(notification, numberOfTransfers , optimal, status, plannedDepartureTime, actualDepartureTime, delayString)
    }

    private fun parseNotificationMessage(jsonTravelOption: JSONObject) : TravelOptionNotification? {

        val firstLeg = getFirstJsonObjectFromArrayIfPresent(jsonTravelOption, LEGS)
        if (firstLeg != null) {
            val firstMessage = getFirstJsonObjectFromArrayIfPresent(firstLeg, MESSAGES)
            if (firstMessage != null) {
                if (firstMessage.has(MESSAGE_TEXT)) {
                    return TravelOptionNotification(true, firstMessage.getString(MESSAGE_TEXT))
                }
            }
        }


        return null
    }

    private fun getFirstJsonObjectFromArrayIfPresent(json: JSONObject, name: String) : JSONObject? {

        if (json.has(name)) {
            val jsonArray = json.getJSONArray(name)
            if (jsonArray.length() > 0) {
                return jsonArray.getJSONObject(0)
            }
        }

        return null
    }

    private fun getPlannedAndCurrentDepartureTimes(json: JSONObject) : Pair<Instant?, Instant?> {
        val firstLeg = getFirstJsonObjectFromArrayIfPresent(json, LEGS)
        if (firstLeg != null) {

            if (firstLeg.has(ORIGIN)) {
                val origin = firstLeg.getJSONObject(ORIGIN)

                var plannedDepartureTimeInstant : Instant? = null
                var actualDepartureTimeInstant : Instant? = null

                if (origin.has(PLANNED_DATE_TIME)) {
                    val plannedDepartureTimeStr = origin.getString(PLANNED_DATE_TIME)
                    plannedDepartureTimeInstant = parseStringToInstant(plannedDepartureTimeStr)
                }

                if (origin.has(ACTUAL_DATE_TIME)) {
                    val actualDepartureTimeStr = origin.getString(ACTUAL_DATE_TIME)
                    actualDepartureTimeInstant = parseStringToInstant(actualDepartureTimeStr)

                }
                return Pair(plannedDepartureTimeInstant, actualDepartureTimeInstant)
            }
        }

        return Pair(null, null)
    }

    private fun getDelayInMinutes(plannedTime: Instant?, actualTime: Instant?) : Long {
        if (plannedTime == null || actualTime == null) {
            return 0
        }

        return Duration.between(plannedTime, actualTime).toMinutes()
    }

    private fun formatDelayString(delayInMinutes: Long) : String? {
        if (delayInMinutes > 0) {
            return "+$delayInMinutes min"
        }
        return null
    }

    private fun parseStatus(travelOptionJson: JSONObject, delayInMinutes: Long) : DisruptionStatus {
        if (delayInMinutes > 0) {
            return DisruptionStatus.DELAYED
        }

        val statusString = travelOptionJson.getString(STATUS)
        return DisruptionStatus.LOOKUP.getOrDefault(statusString, DisruptionStatus.UNKNOWN)
    }

    @SuppressLint("SimpleDateFormat")
    private fun parseStringToInstant(dateTimeStr: String) : Instant? {
        return try {
            SimpleDateFormat(DATETIME_PATTERN).parse(dateTimeStr).toInstant()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse string '$dateTimeStr' to format '${DATETIME_PATTERN}'.", e)
            null
        }
    }

}
