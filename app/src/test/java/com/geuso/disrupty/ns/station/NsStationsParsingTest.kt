package com.geuso.disrupty.ns.station

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.InputStream

/*
 * https://stackoverflow.com/questions/31272732/unit-testing-with-android-xmlpullparser-on-the-jvm
 * https://stackoverflow.com/questions/30629314/android-studio-with-junit-4-12-junit-version-3-8-or-later-expected
 */
@RunWith(RobolectricTestRunner::class)
class NsStationsParsingTest {

    @Test
    fun `Test if NS stations xml message is parsed correctly`() {
        val file = NsStationsParsingTest::class.java.classLoader.getResource("ns/stationslist/stations-list.xml")

        val stations = NsStationsXmlParser().parse(file.content as InputStream)

        assertThat(stations, "Stations list").isNotEmpty()

        val firstStation = stations[0]
        assertThat(firstStation.code, "First station code").isEqualTo("HT")
        assertThat(firstStation.type, "First station type").isEqualTo("knooppuntIntercitystation")
        assertThat(firstStation.name, "First station name").isEqualTo("'s-Hertogenbosch")
        assertThat(firstStation.countryCode, "First station country code").isEqualTo("NL")

        val secondStation = stations[1]
        assertThat(secondStation.code, "Second station code").isEqualTo("HTO")
        assertThat(secondStation.type, "Second station type").isEqualTo("stoptreinstation")
        assertThat(secondStation.name, "Second station name").isEqualTo("'s-Hertogenbosch Oost")
        assertThat(secondStation.countryCode, "Second station country code").isEqualTo("NL")

        val thirdStation = stations[2]
        assertThat(thirdStation.code, "Third station code").isEqualTo("HDE")
        assertThat(thirdStation.type, "Third station type").isEqualTo("stoptreinstation")
        assertThat(thirdStation.name, "Third station name").isEqualTo("'t Harde")
        assertThat(thirdStation.countryCode, "Third station country code").isEqualTo("NL")
    }
}