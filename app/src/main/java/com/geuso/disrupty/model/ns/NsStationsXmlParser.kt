package com.geuso.disrupty.model.ns

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

// https://developer.android.com/training/basics/network-ops/xml.html
class NsStationsXmlParser {

    fun parse(stream: InputStream): List<Station> {

        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(stream, null)


        return ArrayList()
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