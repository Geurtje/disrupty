package com.geuso.disrupty.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.geuso.disrupty.App
import com.geuso.disrupty.model.db.Subscription
import com.geuso.disrupty.model.db.SubscriptionDao
import com.geuso.disrupty.model.db.TimeConverter

@Database(entities = [Subscription::class], version = 1)
@TypeConverters(TimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        private const val DB_NAME = "disrupty-db"
        val INSTANCE: AppDatabase = Room.databaseBuilder<AppDatabase>(App.context, AppDatabase::class.java, DB_NAME).allowMainThreadQueries().build()
    }


}
