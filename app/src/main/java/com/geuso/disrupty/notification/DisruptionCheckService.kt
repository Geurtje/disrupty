package com.geuso.disrupty.notification

import android.app.Service
import android.content.Intent
import android.os.IBinder


class DisruptionCheckService : Service() {

    private val disruptionCheckAlarm = DisruptionCheckAlarm()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        disruptionCheckAlarm.setAlarm(this)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}