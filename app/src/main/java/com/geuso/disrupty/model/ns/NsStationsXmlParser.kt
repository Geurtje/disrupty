package com.geuso.disrupty.model.ns


class NsStationsXmlParser() {

    fun parse(stream: String): List<Station> {



        return ArrayList()
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
}