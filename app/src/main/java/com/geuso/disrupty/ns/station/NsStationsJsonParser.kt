package com.geuso.disrupty.ns.station

import org.json.JSONObject

class NsStationsJsonParser {

    companion object {
        private const val PAYLOAD = "payload"

        private const val STATION_CODE = "code"
        private const val STATION_STATION_TYPE = "stationType"
        private const val STATION_COUNTRY_CODE = "land"

        private const val STATION_NAMES = "namen"
        private const val STATION_NAME_LONG = "lang"
    }

    fun parseStations(json: JSONObject) : List<Station> {
        val stationsList = arrayListOf<Station>()

        if (json.has(PAYLOAD)) {
            val stationsJsonArray = json.getJSONArray(PAYLOAD)

            val numberOfStations = stationsJsonArray.length()
            for (i in 0 until numberOfStations) {
                val station = parseStation(stationsJsonArray.getJSONObject(i))
                stationsList.add(station)
            }
        }

        return stationsList
    }

    private fun parseStation(jsonStation: JSONObject) : Station {
        val code = jsonStation.getString(STATION_CODE)
        val type = jsonStation.getString(STATION_STATION_TYPE)
        val name = extractName(jsonStation)
        val countryCode = jsonStation.getString(STATION_COUNTRY_CODE)

        return Station(code, type, name, countryCode)
    }

    private fun extractName(jsonStation: JSONObject) : String {
        val stationNamesJson = jsonStation.getJSONObject(STATION_NAMES)
        return stationNamesJson.getString(STATION_NAME_LONG)
    }

}