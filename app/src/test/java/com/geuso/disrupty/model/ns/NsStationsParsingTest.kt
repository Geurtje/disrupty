package com.geuso.disrupty.model.ns

import org.junit.Test
import java.io.InputStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NsStationsParsingTest {

    @Test
    fun parseStationsResponse() {

        val file = NsStationsParsingTest::class.java.classLoader.getResource("test-ns-stations.xml")
        val fileContent = (file.content as InputStream).bufferedReader().use { file.readText() }
        print(fileContent)

        val stations = NsStationsXmlParser().parse(fileContent)

        assertTrue(stations.isNotEmpty(), "Stations should not be empty")

        assertEquals(stations[0].code, "HT")
        assertEquals(stations[1].code, "HTO")
        assertEquals(stations[2].code, "HDE")


    }
}