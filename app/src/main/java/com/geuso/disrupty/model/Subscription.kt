package com.geuso.disrupty.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "subscriptions")
data class Subscription (
        @ColumnInfo(name = "station_from") var stationFrom : String = "",
        @ColumnInfo(name = "station_to") var stationTo : String = "",
        @ColumnInfo(name = "time_from_hour") var timeFromHour : Int = 0,
        @ColumnInfo(name = "time_from_minute") var timeFromMinute : Int = 0,
        @ColumnInfo(name = "time_to_hour") var timeToHour : Int = 0,
        @ColumnInfo(name = "time_to_minute") var timeToMinute : Int = 0,
        @ColumnInfo(name = "day_monday") var monday : Boolean = false,
        @ColumnInfo(name = "day_tuesday") var tuesday : Boolean = false,
        @ColumnInfo(name = "day_wednesday") var wednesday : Boolean = false,
        @ColumnInfo(name = "day_thursday") var thursday : Boolean = false,
        @ColumnInfo(name = "day_friday") var friday : Boolean = false,
        @ColumnInfo(name = "day_saturday") var saturday : Boolean = false,
        @ColumnInfo(name = "day_sunday") var sunday : Boolean = false
) {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}