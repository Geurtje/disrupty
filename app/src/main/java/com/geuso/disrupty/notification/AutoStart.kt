package com.geuso.disrupty.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AutoStart : BroadcastReceiver() {

    private val disruptionCheckAlarm = DisruptionCheckAlarm()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.action == Intent.ACTION_BOOT_COMPLETED
            && context != null) {
            disruptionCheckAlarm.setAlarm(context)
        }
    }

}