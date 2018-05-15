package com.geuso.disrupty.disruption.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.geuso.disrupty.util.DateTimeConverter
import java.util.*

@Entity(tableName = "disruptionChecks")
data class DisruptionCheck (
        @ColumnInfo(name = "subscription_id") val subscriptionId: Long,
        @ColumnInfo(name = "date") @TypeConverters(DateTimeConverter::class) val checkTime: Date,
        @ColumnInfo(name = "disrupted") val isDisrupted: Boolean,
        @ColumnInfo(name = "message") var message: String?,
        @ColumnInfo(name = "response") val response: String,
        @ColumnInfo(name = "success") val success: Boolean = true

) {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
