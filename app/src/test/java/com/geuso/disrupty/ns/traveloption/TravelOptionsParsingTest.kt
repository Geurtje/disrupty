package com.geuso.disrupty.ns.traveloption

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.geuso.disrupty.ns.instantOf
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.InputStream

@RunWith(RobolectricTestRunner::class)
class TravelOptionsParsingTest {

    @Test
    fun `Test if NS travel options xml message is parsed correctly`() {
        val file = TravelOptionsParsingTest::class.java.classLoader.getResource("ns/traveloption/travel-options-response.xml")

        val travelOptions = TravelOptionXmlParser().parse(file.content as InputStream)

        assertThat(travelOptions, "travel options list").hasSize(4)

        val firstTravelOption = travelOptions[0]
        assertThat(firstTravelOption.numberOfTransfers, "First travel option number of transfers").isEqualTo(1)
        assertThat(firstTravelOption.optimal, "First travel option optimal").isEqualTo(false)
        assertThat(firstTravelOption.disruptionStatus, "First travel option disruptionStatus").isEqualTo(DisruptionStatus.NEW)
        assertThat(firstTravelOption.notification, "First travel option notification").isNull()


        val secondTravelOption = travelOptions[1]
        assertThat(secondTravelOption.numberOfTransfers, "Second travel option number of transfers").isEqualTo(0)
        assertThat(secondTravelOption.optimal, "Second travel option optimal").isEqualTo(false)
        assertThat(secondTravelOption.disruptionStatus, "Second travel option disruptionStatus").isEqualTo(DisruptionStatus.NOT_POSSIBLE)
        assertThat(secondTravelOption.notification!!.severe, "Second travel option notification severe").isEqualTo(true)
        assertThat(secondTravelOption.notification!!.text, "Second travel option notification text").isEqualTo("Dit reisadvies vervalt")

        val thirdTravelOption = travelOptions[2]
        assertThat(thirdTravelOption.numberOfTransfers, "Second travel option number of transfers").isEqualTo(1)
        assertThat(thirdTravelOption.optimal, "Second travel option optimal").isEqualTo(false)
        assertThat(thirdTravelOption.disruptionStatus, "Second travel option disruptionStatus").isEqualTo(DisruptionStatus.CHANGED)
        assertThat(thirdTravelOption.notification!!.severe, "Second travel option notification severe").isEqualTo(false)
        assertThat(thirdTravelOption.notification!!.text, "Second travel option notification text").isEqualTo("Dit is een aangepast reisadvies")

        val fourthTravelOption = travelOptions[3]
        assertThat(fourthTravelOption.numberOfTransfers, "Fourth travel option number of transfers").isEqualTo(1)
        assertThat(fourthTravelOption.optimal, "Fourth travel option optimal").isEqualTo(true)
        assertThat(fourthTravelOption.disruptionStatus, "Fourth travel option disruptionStatus").isEqualTo(DisruptionStatus.ACCORDING_TO_PLAN)
        assertThat(fourthTravelOption.notification, "Fourth travel option notification").isNull()

    }


    @Test
    fun `Test if delayed NS travel options xml message is parsed correctly`() {
        val file = TravelOptionsParsingTest::class.java.classLoader.getResource("ns/traveloption/travel-options-delayed-response.xml")
        val travelOptions = TravelOptionXmlParser().parse(file.content as InputStream)

        assertThat(travelOptions, "travel options list").hasSize(1)

        val firstTravelOption = travelOptions[0]

        assertThat(firstTravelOption.optimal, "First travel option optimal").isEqualTo(false)
        assertThat(firstTravelOption.disruptionStatus, "First travel option disruptionStatus").isEqualTo(DisruptionStatus.DELAYED)
        assertThat(firstTravelOption.notification!!.severe, "First travel option notification severe").isEqualTo(true)
        assertThat(firstTravelOption.notification!!.text, "First travel option notification text").isEqualTo("Let op, latere aankomst")

        assertThat(firstTravelOption.plannedDepartureTime, "First travel option planned departure time").isEqualTo(instantOf("2018-11-17T20:35:00+0100"))
        assertThat(firstTravelOption.currentDepartureTime, "First travel option current departure time").isEqualTo(instantOf("2018-11-17T20:39:00+0100"))
        assertThat(firstTravelOption.departureDelay, "First travel option departure delay").isEqualTo("+4 min")
    }

}