package com.geuso.disrupty.ns

import com.geuso.disrupty.ns.traveloption.TravelOptionsParsingTest
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.Instant

fun instantOf(dateTimeStr: String): Instant {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    return dateFormat.parse(dateTimeStr).toInstant()
}

fun readResourceAsJsonObject(resourceName: String) : JSONObject {
    val file = TravelOptionsParsingTest::class.java.classLoader.getResource(resourceName).content as InputStream
    val fileContents = file.bufferedReader().use(BufferedReader::readText)
    return JSONObject(fileContents)
}