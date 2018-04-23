package com.geuso.disrupty.util

import android.arch.persistence.room.TypeConverter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * TypeConverter for the date time usages in a disruption check.
 * Unable to make this an object as the Room dao requires a public constructor.
 */
class DateTimeConverter {

    companion object {
        private val DATE_TIME_FORMAT : DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val INSTANCE : DateTimeConverter = DateTimeConverter()
    }


    //These should be in the companion object but then Room isn't able to find any @TypeConverter functions
    @TypeConverter
    fun fromDateTimeString(value: String) : Date {
        return DATE_TIME_FORMAT.parse(value)
    }

    @TypeConverter
    fun stringToDateTime(value: Date) : String{
        return DATE_TIME_FORMAT.format(value)
    }

}