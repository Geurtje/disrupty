package com.geuso.disrupty.ns.traveloption

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.geuso.disrupty.ns.station.NsStationsJsonParser
import com.geuso.disrupty.ns.station.NsStationsParsingTest
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.BufferedReader
import java.io.InputStream


@RunWith(RobolectricTestRunner::class)
class PublicTravelTravelOptionsParsingTest {

    companion object {
        private const val STATION_TYPE_INTERCITY = "KNOOPPUNT_INTERCITY_STATION"
        private const val STATION_TYPE_STOPTRAIN = "STOPTREIN_STATION"
    }

    @Test
    fun `Test if NS travel options json message is parsed correctly`() {
        val file = TravelOptionsParsingTest::class.java.classLoader.getResource("ns/traveloption/travel-options-response.json").content as InputStream
        val fileContents = file.bufferedReader().use(BufferedReader::readText)
        val json = JSONObject(fileContents)

        val travelOptions = TravelOptionJsonParser().parseTravelOptions(json)

        assertThat(travelOptions, "travel options list").hasSize(2)

        val firstTravelOption = travelOptions[0]
        assertThat(firstTravelOption.numberOfTransfers, "First travel option number of transfers").isEqualTo(0)
        assertThat(firstTravelOption.optimal, "First travel option optimal").isEqualTo(false)
        assertThat(firstTravelOption.disruptionStatus, "First travel option disruptionStatus").isEqualTo(DisruptionStatus.NORMAL)
        assertThat(firstTravelOption.notification, "First travel option notification").isNull()


        val secondTravelOption = travelOptions[1]
        assertThat(secondTravelOption.numberOfTransfers, "Second travel option number of transfers").isEqualTo(0)
        assertThat(secondTravelOption.optimal, "Second travel option optimal").isEqualTo(false)
        assertThat(secondTravelOption.disruptionStatus, "Second travel option disruptionStatus").isEqualTo(DisruptionStatus.ADDITIONAL)
        assertThat(secondTravelOption.notification, "Second travel option notification").isNull()

    }
}