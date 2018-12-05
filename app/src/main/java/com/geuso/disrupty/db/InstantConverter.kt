package com.geuso.disrupty.db

import android.arch.persistence.room.TypeConverter
import java.text.DateFormat
import java.time.Instant
import java.util.*

/**
 * TypeConverter for the java.time.Instant usages in a disruption check.
 * The Instant is converted to/from epoch milliseconds.
 */
class InstantConverter {

    @TypeConverter
    fun longToInstant(value: Long) : Instant {
        return Instant.ofEpochMilli(value)
    }

    @TypeConverter
    fun instantToLong(instant: Instant) : Long {
        return instant.toEpochMilli();
    }

    fun format(instant: Instant) : String {
        val format = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.getDefault())
        return format.format(instant.toEpochMilli())
    }

}