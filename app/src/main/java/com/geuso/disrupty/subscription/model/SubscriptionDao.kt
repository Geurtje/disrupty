package com.geuso.disrupty.subscription.model

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

    @Query("DELETE FROM subscriptions WHERE id = :id")
    fun deleteSubscriptionById(id: Long): Int

    @Query("SELECT * FROM subscriptions")
    fun getAllSubscriptions(): List<Subscription>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    fun getSubscriptionById(id: Long): Subscription?

}