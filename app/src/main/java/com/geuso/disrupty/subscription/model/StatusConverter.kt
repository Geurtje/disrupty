package com.geuso.disrupty.subscription.model

import android.arch.persistence.room.TypeConverter


class StatusConverter {

    companion object {
        val INSTANCE : StatusConverter = StatusConverter()
    }


    //These should be in the companion object but then Room isn't able to find any @TypeConverter functions
    @TypeConverter
    fun stringToStatus(value: String) : Status {
        return Status.LOOKUP.getOrDefault(value, Status.UNKNOWN)
    }

    @TypeConverter
    fun statusToString(value: Status) : String{
        return value.key
    }

}