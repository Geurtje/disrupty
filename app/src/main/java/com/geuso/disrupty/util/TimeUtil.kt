package com.geuso.disrupty.util

import android.util.Log
import java.util.*


fun extractHourAndMinuteFromText(text: CharSequence) : Pair<Int, Int> {
    val textParts : List<String> = text.split(":")

    if (textParts.size >= 2) {
        try {
            val hour = Integer.valueOf(textParts[0])
            val minute = Integer.valueOf(textParts[1])
            return Pair(hour, minute)
        }
        catch (e: NumberFormatException){
            Log.i("extractHourAndMinuteFromText", "Unable to extract time from button text '$text'. Falling back to current time.")
        }
    }

    val calendar = Calendar.getInstance()
    return Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
}