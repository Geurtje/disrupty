package com.geuso.disrupty.disruption.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.geuso.disrupty.db.InstantConverter
import java.time.Instant

@Entity(tableName = "disruptionChecks")
data class DisruptionCheck (
        @ColumnInfo(name = "subscription_id") val subscriptionId: Long,
        @ColumnInfo(name = "timestamp") @TypeConverters(InstantConverter::class) val timestamp: Instant = Instant.now(),
        @ColumnInfo(name = "disrupted") val isDisrupted: Boolean,
        @ColumnInfo(name = "message") var message: String?,
        @ColumnInfo(name = "response") val response: String,
        @ColumnInfo(name = "success") val success: Boolean = true
) {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
