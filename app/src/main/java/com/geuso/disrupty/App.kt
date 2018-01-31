package com.geuso.disrupty

import android.app.Application
import android.content.Context

/**
 * Created by Tom on 31-1-2018.
 */
class App : Application() {

    companion object {
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}