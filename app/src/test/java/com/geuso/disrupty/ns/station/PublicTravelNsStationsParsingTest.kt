package com.geuso.disrupty.ns.station

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.geuso.disrupty.ns.readResourceAsJsonObject
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PublicTravelNsStationsParsingTest {

    companion object {
        private const val STATION_TYPE_INTERCITY = "KNOOPPUNT_INTERCITY_STATION"
        private const val STATION_TYPE_STOPTRAIN = "STOPTREIN_STATION"
    }

    @Test
    fun `Test if NS stations json message is parsed correctly`() {
        val json = readResourceAsJsonObject("ns/stationslist/stations-list-v2.json")
        val stations = NsStationsJsonParser().parseStations(json)

        assertThat(stations, "Stations list").isNotEmpty()

        val firstStation = stations[0]
        assertThat(firstStation.code, "First station code").isEqualTo("HT")
        assertThat(firstStation.type, "First station type").isEqualTo(STATION_TYPE_INTERCITY)
        assertThat(firstStation.name, "First station name").isEqualTo("'s-Hertogenbosch")
        assertThat(firstStation.countryCode, "First station country code").isEqualTo("NL")

        val secondStation = stations[1]
        assertThat(secondStation.code, "Second station code").isEqualTo("HTO")
        assertThat(secondStation.type, "Second station type").isEqualTo(STATION_TYPE_STOPTRAIN)
        assertThat(secondStation.name, "Second station name").isEqualTo("'s-Hertogenbosch Oost")
        assertThat(secondStation.countryCode, "Second station country code").isEqualTo("NL")

        val thirdStation = stations[2]
        assertThat(thirdStation.code, "Third station code").isEqualTo("HDE")
        assertThat(thirdStation.type, "Third station type").isEqualTo(STATION_TYPE_STOPTRAIN)
        assertThat(thirdStation.name, "Third station name").isEqualTo("'t Harde")
        assertThat(thirdStation.countryCode, "Third station country code").isEqualTo("NL")

    }

}