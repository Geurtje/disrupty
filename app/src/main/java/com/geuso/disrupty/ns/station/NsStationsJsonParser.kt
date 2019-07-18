package com.geuso.disrupty.ns.station

import org.json.JSONArray
import org.json.JSONObject

class NsStationsJsonParser {

    companion object {
        private const val STATION_CODE = "code"
        private const val STATION_STATION_TYPE = "stationType"
        private const val STATION_COUNTRY_CODE = "land"

        private const val STATION_NAMES = "namen"
        private const val STATION_NAME_LONG = "lang"
    }

    fun parseStations(json: JSONObject) : List<Station> {
        val stationsList = arrayListOf<Station>()

        val stationsJsonArray = json.get("payload") as JSONArray

        val numberOfStations = stationsJsonArray.length()
        for (i in 0 until numberOfStations) {
            val station = parseStation(stationsJsonArray.get(i) as JSONObject)
            stationsList.add(station)
        }

        return stationsList
    }

    private fun parseStation(jsonStation: JSONObject) : Station {
        val code = jsonStation.get(STATION_CODE).toString()
        val type = jsonStation.get(STATION_STATION_TYPE).toString()
        val name = extractName(jsonStation)
        val countryCode = jsonStation.get(STATION_COUNTRY_CODE).toString()

        return Station(code, type, name, countryCode)
    }

    private fun extractName(jsonStation: JSONObject) : String {
        val stationNamesJson = jsonStation.get(STATION_NAMES) as JSONObject
        return stationNamesJson.get(STATION_NAME_LONG).toString()
    }

}