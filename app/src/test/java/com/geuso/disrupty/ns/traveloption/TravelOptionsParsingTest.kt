package com.geuso.disrupty.ns.traveloption

import assertk.assert
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.InputStream

@RunWith(RobolectricTestRunner::class)
class TravelOptionsParsingTest {

    @Test
    fun parseTravelOptionsResponse() {
        val file = TravelOptionsParsingTest::class.java.classLoader.getResource("ns/traveloption/travel-options-response.xml")

        val travelOptions = TravelOptionXmlParser().parse(file.content as InputStream)

        assert(travelOptions, "travel options list").hasSize(4)

        val firstTravelOption = travelOptions[0]
        assert(firstTravelOption.numberOfTransfers, "First travel option number of transfers").isEqualTo(1)
        assert(firstTravelOption.optimal, "First travel option optimal").isEqualTo(false)
        assert(firstTravelOption.status, "First travel option status").isEqualTo(Status.NEW)
        assert(firstTravelOption.notification, "First travel option notification").isEqualTo(null)


        val secondTravelOption = travelOptions[1]
        assert(secondTravelOption.numberOfTransfers, "Second travel option number of transfers").isEqualTo(0)
        assert(secondTravelOption.optimal, "Second travel option optimal").isEqualTo(false)
        assert(secondTravelOption.status, "Second travel option status").isEqualTo(Status.NOT_POSSIBLE)
        assert(secondTravelOption.notification!!.severe, "Second travel option notification severe").isEqualTo(true)
        assert(secondTravelOption.notification!!.text, "Second travel option notification text").isEqualTo("Dit reisadvies vervalt")

        val thirdTravelOption = travelOptions[2]
        assert(thirdTravelOption.numberOfTransfers, "Second travel option number of transfers").isEqualTo(1)
        assert(thirdTravelOption.optimal, "Second travel option optimal").isEqualTo(false)
        assert(thirdTravelOption.status, "Second travel option status").isEqualTo(Status.CHANGED)
        assert(thirdTravelOption.notification!!.severe, "Second travel option notification severe").isEqualTo(false)
        assert(thirdTravelOption.notification!!.text, "Second travel option notification text").isEqualTo("Dit is een aangepast reisadvies")

        val fourthTravelOption = travelOptions[3]
        assert(fourthTravelOption.numberOfTransfers, "Fourth travel option number of transfers").isEqualTo(1)
        assert(fourthTravelOption.optimal, "Fourth travel option optimal").isEqualTo(true)
        assert(fourthTravelOption.status, "Fourth travel option status").isEqualTo(Status.ACCORDING_TO_PLAN)
        assert(fourthTravelOption.notification, "Fourth travel option notification").isEqualTo(null)

    }
}