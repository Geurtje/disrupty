package com.geuso.disrupty.ns.traveloption

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.geuso.disrupty.ns.instantOf
import com.geuso.disrupty.ns.readResourceAsJsonObject
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class PublicTravelTravelOptionsParsingTest {

    @Test
    fun `Test if NS travel options json message is parsed correctly`() {
        val json = readResourceAsJsonObject("ns/traveloption/travel-options-response.json")
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

    @Test
    fun `Test if disrupted NS travel options json message is parsed correctly`() {
        val json = readResourceAsJsonObject("ns/traveloption/travel-options-disrupted-response.json")
        val travelOptions = TravelOptionJsonParser().parseTravelOptions(json)

        assertThat(travelOptions, "travel options list").hasSize(2)

        val firstTravelOption = travelOptions[0]

        assertThat(firstTravelOption.optimal, "First travel option optimal").isEqualTo(false)
        assertThat(firstTravelOption.disruptionStatus, "First travel option disruptionStatus").isEqualTo(DisruptionStatus.CANCELLED)
        assertThat(firstTravelOption.notification, "First travel option notification").isNotNull()
        assertThat(firstTravelOption.notification!!.severe, "First travel option notification severe").isEqualTo(true)
        assertThat(firstTravelOption.notification!!.text, "First travel option notification text").isEqualTo("Tussen Eindhoven en Weert rijden er minder Intercity's door een seinstoring.")

        val secondTravelOption = travelOptions[1]
        assertThat(secondTravelOption.optimal, "Second travel option optimal").isEqualTo(true)
        assertThat(secondTravelOption.disruptionStatus, "Second travel option disruptionStatus").isEqualTo(DisruptionStatus.DISRUPTION)
        assertThat(secondTravelOption.notification, "Second travel option notification").isNotNull()
        assertThat(secondTravelOption.notification!!.severe, "Second travel option notification severe").isEqualTo(true)
        assertThat(secondTravelOption.notification!!.text, "Second travel option notification text").isEqualTo("Tussen Eindhoven en Weert rijden er minder Intercity's door een seinstoring.")
    }

    @Test
    fun `Test if delayed NS travel options json message is parsed correctly`() {
        val json = readResourceAsJsonObject("ns/traveloption/travel-options-delayed-response.json")
        val travelOptions = TravelOptionJsonParser().parseTravelOptions(json)

        assertThat(travelOptions, "travel options list").hasSize(2)

        val firstTravelOption = travelOptions[0]
        assertThat(firstTravelOption.optimal, "First travel option optimal").isEqualTo(true)
        assertThat(firstTravelOption.disruptionStatus, "First travel option disruptionStatus").isEqualTo(DisruptionStatus.NORMAL)
        assertThat(firstTravelOption.notification, "First travel option notification").isNull()

        val secondTravelOption = travelOptions[1]
        assertThat(secondTravelOption.optimal, "Second travel option optimal").isEqualTo(false)
        assertThat(secondTravelOption.disruptionStatus, "Second travel option disruptionStatus").isEqualTo(DisruptionStatus.DELAYED)
        assertThat(secondTravelOption.notification, "Second travel option notification").isNull()
        assertThat(secondTravelOption.plannedDepartureTime, "Second travel option planned departure time").isEqualTo(instantOf("2019-08-13T07:18:00+0200"))
        assertThat(secondTravelOption.currentDepartureTime, "Second travel option current departure time").isEqualTo(instantOf("2019-08-13T07:24:00+0200"))
        assertThat(secondTravelOption.departureDelay, "Second travel option departure delay").isEqualTo("+6 min")
    }

}