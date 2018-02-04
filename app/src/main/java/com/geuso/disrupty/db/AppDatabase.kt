package com.geuso.disrupty.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.geuso.disrupty.App
import com.geuso.disrupty.model.Subscription
import com.geuso.disrupty.model.SubscriptionDao

@Database(entities = arrayOf(Subscription::class), version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        private val DB_NAME = "disrupty-db"
        val INSTANCE: AppDatabase = Room.databaseBuilder<AppDatabase>(App.context, AppDatabase::class.java, DB_NAME).allowMainThreadQueries().build()
    }


}
