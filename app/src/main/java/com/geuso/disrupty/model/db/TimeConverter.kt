package com.geuso.disrupty.model.db

import android.arch.persistence.room.TypeConverter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * TypeConverter for the time usages in a subscription.
 * Unable to make this an object as the Room dao requires a public constructor.
 */
class TimeConverter {

    companion object {
        private val TIME_FORMAT : DateFormat = SimpleDateFormat("HH:mm")
        val INSTANCE : TimeConverter = TimeConverter()
    }


    //These should be in the companion object but then Room isn't able to find any @TypeConverter functions
    @TypeConverter
    fun fromTimeString(value: String) : Date {
        return TIME_FORMAT.parse(value)
    }

    @TypeConverter
    fun dateToTime(value: Date) : String{
        return TIME_FORMAT.format(value)
    }

}