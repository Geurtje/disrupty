package com.geuso.disrupty.model.ns

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

// https://developer.android.com/training/basics/network-ops/xml.html
class NsStationsXmlParser {

    companion object {
        private val namespace : String? = null
    }

    fun parse(stream: InputStream): List<Station> {

        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(stream, null)

        parser.nextTag()
        return readFeed(parser)
    }

    fun readFeed(parser: XmlPullParser) : List<Station> {
        val stations = ArrayList<Station>()

        parser.require(XmlPullParser.START_TAG, namespace, "Stations")

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            if ("Station" == name) {
                stations.add(readStation(parser))
            }
        }

        return stations
    }

    private fun readStation(parser: XmlPullParser): Station {
        parser.require(XmlPullParser.START_TAG, namespace, "Station")

        var code = ""
        var type = ""
        var name = ""
        var countryCode = ""

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val elementName = parser.name
            when (elementName) {
                "Code" -> code = readCode(parser)
                "Type" -> type = readType(parser)
                "Namen" -> name = readName(parser)
                "Land" -> countryCode = readCountryCode(parser)
                else -> skip(parser)
            }
        }
        return Station(code, type, name, countryCode)
    }

    private fun readCode(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, namespace, "Code")
        val code = readText(parser)
        parser.require(XmlPullParser.END_TAG, namespace, "Code")
        return code
    }

    private fun readType(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, namespace, "Type")
        val type = readText(parser)
        parser.require(XmlPullParser.END_TAG, namespace, "Type")
        return type
    }

    /**
     * Extracts text from element 'Lang' in the following xml snippet:
     *
     *  <Namen>
     *      <Kort>Short name</Kort>
     *      <Middel>Middle name</Middel>
     *       <Lang>Long name</Lang>
     *  </Namen>
     */
    private fun readName(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, namespace, "Namen")

        var name = ""

        if (parser.next() != XmlPullParser.END_TAG) {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                val elementName = parser.name
                when (elementName) {
                    "Lang" -> name = readText(parser)
                    else -> skip(parser)
                }
            }
        }

        parser.require(XmlPullParser.END_TAG, namespace, "Namen")
        return name
    }


    private fun readCountryCode(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, namespace, "Land")
        val countryCode = readText(parser)
        parser.require(XmlPullParser.END_TAG, namespace, "Land")
        return countryCode
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

//    private fun readFeed(parser: XmlPullParser): List<Station> {
//        val entries = ArrayList<Station>()
//
//        parser.require(XmlPullParser.START_TAG, ns, "feed")
//        while (parser.next() !== XmlPullParser.END_TAG) {
//            if (parser.getEventType() !== XmlPullParser.START_TAG) {
//                continue
//            }
//            val name = parser.getName()
//            // Starts by looking for the entry tag
//            if (name == "entry") {
//                entries.add(readEntry(parser))
//            } else {
//                skip(parser)
//            }
//        }
//        return entries
//    }