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

    // Note that the id parameter name isn't preserved and it's called arg0 in the query.
    @Query("DELETE FROM subscriptions WHERE id = :id")
    fun deleteSubscriptionById(id: Long): Int


    @Query("SELECT * FROM subscriptions")
    fun getAllSubscriptions(): Array<Subscription>

    // Note that the id parameter name isn't preserved and it's called arg0 in the query.
    @Query("SELECT * FROM subscriptions WHERE id = :id")
    fun getSubscriptionById(id: Long): Subscription


}