package com.geuso.disrupty.ns.traveloption

import org.json.JSONObject
import java.time.Instant

class TravelOptionJsonParser {

    companion object {
        private const val TRIPS = "trips"

        private const val TRANSFERS = "transfers"
        private const val OPTIMAL = "optimal"
        private const val STATUS = "status"

        private const val STATION_NAMES = "namen"
        private const val STATION_NAME_LONG = "lang"
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
        val statusString = jsonTravelOption.getString(STATUS)
        val status = DisruptionStatus.LOOKUP.getOrDefault(statusString, DisruptionStatus.UNKNOWN)

        return TravelOption(null, numberOfTransfers , optimal, status, Instant.now(), Instant.now())
    }

}