package com.geuso.disrupty.disruption.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query


@Dao
interface DisruptionCheckDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertDisruptionCheck(disruptionCheck: DisruptionCheck): Long

    @Query("SELECT * FROM disruptionChecks ORDER BY strftime('%s', date) desc LIMIT 50")
    fun getLatestDisruptionChecks() : List<DisruptionCheck>

}