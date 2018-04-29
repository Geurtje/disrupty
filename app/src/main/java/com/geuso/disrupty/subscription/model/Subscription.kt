package com.geuso.disrupty.subscription.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import java.util.*


@Entity(tableName = "subscriptions")
data class Subscription (
        @ColumnInfo(name = "station_from") var stationFrom : String = "",
        @ColumnInfo(name = "station_to") var stationTo : String = "",
        @ColumnInfo(name = "time_from") @TypeConverters(TimeConverter::class) var timeFrom : Date,
        @ColumnInfo(name = "time_to") @TypeConverters(TimeConverter::class) var timeTo : Date,
        @ColumnInfo(name = "day_monday") var monday : Boolean = false,
        @ColumnInfo(name = "day_tuesday") var tuesday : Boolean = false,
        @ColumnInfo(name = "day_wednesday") var wednesday : Boolean = false,
        @ColumnInfo(name = "day_thursday") var thursday : Boolean = false,
        @ColumnInfo(name = "day_friday") var friday : Boolean = false,
        @ColumnInfo(name = "day_saturday") var saturday : Boolean = false,
        @ColumnInfo(name = "day_sunday") var sunday : Boolean = false,
        @ColumnInfo(name = "status") @TypeConverters(StatusConverter::class) var status : Status = Status.UNKNOWN
) {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}



enum class Status(val key: String) {
    UNKNOWN("unknown"),
    OK("ok"),
    NOT_OK("not-ok");

    companion object {
        val LOOKUP : Map<String, Status>

        init {
            LOOKUP = HashMap()
            for (status in Status.values()) {
                LOOKUP[status.key] = status
            }
        }
    }

}