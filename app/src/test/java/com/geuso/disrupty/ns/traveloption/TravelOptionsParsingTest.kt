package com.geuso.disrupty.ns.traveloption

import assertk.assert
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.geuso.disrupty.ns.instantOf
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.Instant

@RunWith(RobolectricTestRunner::class)
class TravelOptionsParsingTest {

    @Test
    fun `Test if NS travel options xml message is parsed correctly`() {
        val file = TravelOptionsParsingTest::class.java.classLoader.getResource("ns/traveloption/travel-options-response.xml")

        val travelOptions = TravelOptionXmlParser().parse(file.content as InputStream)

        assert(travelOptions, "travel options list").hasSize(4)

        val firstTravelOption = travelOptions[0]
        assert(firstTravelOption.numberOfTransfers, "First travel option number of transfers").isEqualTo(1)
        assert(firstTravelOption.optimal, "First travel option optimal").isEqualTo(false)
        assert(firstTravelOption.disruptionStatus, "First travel option disruptionStatus").isEqualTo(DisruptionStatus.NEW)
        assert(firstTravelOption.notification, "First travel option notification").isNull()


        val secondTravelOption = travelOptions[1]
        assert(secondTravelOption.numberOfTransfers, "Second travel option number of transfers").isEqualTo(0)
        assert(secondTravelOption.optimal, "Second travel option optimal").isEqualTo(false)
        assert(secondTravelOption.disruptionStatus, "Second travel option disruptionStatus").isEqualTo(DisruptionStatus.NOT_POSSIBLE)
        assert(secondTravelOption.notification!!.severe, "Second travel option notification severe").isEqualTo(true)
        assert(secondTravelOption.notification!!.text, "Second travel option notification text").isEqualTo("Dit reisadvies vervalt")

        val thirdTravelOption = travelOptions[2]
        assert(thirdTravelOption.numberOfTransfers, "Second travel option number of transfers").isEqualTo(1)
        assert(thirdTravelOption.optimal, "Second travel option optimal").isEqualTo(false)
        assert(thirdTravelOption.disruptionStatus, "Second travel option disruptionStatus").isEqualTo(DisruptionStatus.CHANGED)
        assert(thirdTravelOption.notification!!.severe, "Second travel option notification severe").isEqualTo(false)
        assert(thirdTravelOption.notification!!.text, "Second travel option notification text").isEqualTo("Dit is een aangepast reisadvies")

        val fourthTravelOption = travelOptions[3]
        assert(fourthTravelOption.numberOfTransfers, "Fourth travel option number of transfers").isEqualTo(1)
        assert(fourthTravelOption.optimal, "Fourth travel option optimal").isEqualTo(true)
        assert(fourthTravelOption.disruptionStatus, "Fourth travel option disruptionStatus").isEqualTo(DisruptionStatus.ACCORDING_TO_PLAN)
        assert(fourthTravelOption.notification, "Fourth travel option notification").isNull()

    }


    @Test
    fun `Test if delayed NS travel options xml message is parsed correctly`() {
        val file = TravelOptionsParsingTest::class.java.classLoader.getResource("ns/traveloption/travel-options-delayed-response.xml")
        val travelOptions = TravelOptionXmlParser().parse(file.content as InputStream)

        assert(travelOptions, "travel options list").hasSize(1)

        val firstTravelOption = travelOptions[0]

        assert(firstTravelOption.optimal, "First travel option optimal").isEqualTo(false)
        assert(firstTravelOption.disruptionStatus, "First travel option disruptionStatus").isEqualTo(DisruptionStatus.DELAYED)
        assert(firstTravelOption.notification!!.severe, "First travel option notification severe").isEqualTo(true)
        assert(firstTravelOption.notification!!.text, "First travel option notification text").isEqualTo("Let op, latere aankomst")

        assert(firstTravelOption.plannedDepartureTime, "First travel option planned departure time").isEqualTo(instantOf("2018-11-17T20:35:00+0100"))
        assert(firstTravelOption.currentDepartureTime, "First travel option current departure time").isEqualTo(instantOf("2018-11-17T20:39:00+0100"))
        assert(firstTravelOption.departureDelay, "First travel option departure delay").isEqualTo("+4 min")
    }

}