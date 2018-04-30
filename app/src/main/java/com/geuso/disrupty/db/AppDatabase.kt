package com.geuso.disrupty.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.geuso.disrupty.App
import com.geuso.disrupty.disruption.model.DisruptionCheck
import com.geuso.disrupty.disruption.model.DisruptionCheckDao
import com.geuso.disrupty.subscription.model.StatusConverter
import com.geuso.disrupty.subscription.model.Subscription
import com.geuso.disrupty.subscription.model.SubscriptionDao
import com.geuso.disrupty.subscription.model.TimeConverter

@Database(entities = [Subscription::class, DisruptionCheck::class], version = 1)
@TypeConverters(TimeConverter::class, StatusConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun disruptionCheckDao(): DisruptionCheckDao

    companion object {
        private const val DB_NAME = "disrupty-db"
        val INSTANCE: AppDatabase = Room.databaseBuilder<AppDatabase>(App.context, AppDatabase::class.java, DB_NAME).allowMainThreadQueries().build()
    }


}
