package com.geuso.disrupty.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AutoStart : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        DisruptionCheckJobScheduler.scheduleDisruptionCheckJobIfRequired()
    }

}