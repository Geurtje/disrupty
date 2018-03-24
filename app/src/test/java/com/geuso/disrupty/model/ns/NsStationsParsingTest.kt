package com.geuso.disrupty.model.ns

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.InputStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/*
 * https://stackoverflow.com/questions/31272732/unit-testing-with-android-xmlpullparser-on-the-jvm
 * https://stackoverflow.com/questions/30629314/android-studio-with-junit-4-12-junit-version-3-8-or-later-expected
 */
@RunWith(RobolectricTestRunner::class)
class NsStationsParsingTest {

    @Test
    fun parseStationsResponse() {

        val file = NsStationsParsingTest::class.java.classLoader.getResource("test-ns-stations.xml")
        val fileContent = (file.content as InputStream).bufferedReader().use { file.readText() }
        print(fileContent)

        val stations = NsStationsXmlParser().parse(file.content as InputStream)

        assertTrue(stations.isNotEmpty(), "Stations should not be empty")

        assertEquals(stations[0].code, "HT")
        assertEquals(stations[1].code, "HTO")
        assertEquals(stations[2].code, "HDE")


    }
}