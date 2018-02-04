package com.geuso.disrupty.model

import android.arch.persistence.room.*


@Dao
interface SubscriptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertSubscription(subscription: Subscription): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubscription(subscription: Subscription): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubscriptions(vararg subscriptions: Subscription): List<Long>

    @Update
    fun updateSubscription(subscription: Subscription): Int


    @Delete
    fun deleteSubscription(subscription: Subscription): Int


    @Query("SELECT * FROM subscriptions")
    fun loadAllSubscriptions(): Array<Subscription>


}