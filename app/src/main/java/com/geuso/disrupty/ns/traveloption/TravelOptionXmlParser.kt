package com.geuso.disrupty.ns.traveloption

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.util.*

class TravelOptionXmlParser {

    companion object {
        private val NAMESPACE : String? = null
    }

    fun parse(stream: InputStream): List<TravelOption> {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(stream, null)

        parser.nextTag()
        return readFeed(parser)
    }

    fun readFeed(parser: XmlPullParser) : List<TravelOption> {
        val travelOptions = ArrayList<TravelOption>()

        parser.require(XmlPullParser.START_TAG, NAMESPACE, "ReisMogelijkheden")

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            if ("ReisMogelijkheid" == name) {
                travelOptions.add(readTravelOption(parser))
            }
        }

        return travelOptions
    }

    fun readTravelOption(parser: XmlPullParser): TravelOption {

        var notification : TravelOptionNotification? = null
        var numberOfTransfers = 0
        var optimal = false
        var status = Status.ACCORDING_TO_PLAN


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val elementName = parser.name
            when (elementName) {
                "Melding" -> notification = readNotification(parser)
                "AantalOverstappen" -> numberOfTransfers = readNumberOfTransfers(parser)
                "Status" -> status = readStatus(parser)
                "Optimaal" -> optimal = readOptimal(parser)
                else -> skip(parser)
            }
        }

        return TravelOption(notification, numberOfTransfers, optimal, status)
    }

    private fun readNotification(parser: XmlPullParser): TravelOptionNotification {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "Melding")

        var severe = false
        var text = ""

        if (parser.next() != XmlPullParser.END_TAG) {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                val elementName = parser.name
                when (elementName) {
                    "Ernstig" -> severe = readText(parser).toBoolean()
                    "Text" -> text = readText(parser)
                    else -> skip(parser)
                }
            }
        }

        return TravelOptionNotification(severe, text)
    }

    private fun readNumberOfTransfers(parser: XmlPullParser): Int {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "AantalOverstappen")
        val numberOfTransfers = readText(parser)
        parser.require(XmlPullParser.END_TAG, NAMESPACE, "AantalOverstappen")

        return try {
            numberOfTransfers.toInt()
        }
        catch (e: NumberFormatException ){
            0
        }
    }

    private fun readStatus(parser: XmlPullParser): Status {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "Status")
        val status = readText(parser)
        parser.require(XmlPullParser.END_TAG, NAMESPACE, "Status")

        return Status.LOOKUP.getOrDefault(status, Status.UNKOWN)
    }

    private fun readOptimal(parser: XmlPullParser): Boolean {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "Optimaal")
        val optimal = readText(parser)
        parser.require(XmlPullParser.END_TAG, NAMESPACE, "Optimaal")

        return optimal.toBoolean()
    }

    /**
     * Extracts text from an xml element.
     */
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}
