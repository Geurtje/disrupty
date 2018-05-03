package com.geuso.disrupty.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import com.geuso.disrupty.App
import com.geuso.disrupty.disruption.DisruptionService

class DisruptionCheckAlarm : BroadcastReceiver() {

    companion object {
        private val TAG = DisruptionCheckAlarm::class.qualifiedName
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        DisruptionService.notifyDisruptedSubscriptions()
    }

    fun setAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent("disrupty.START_DISRUPTION_CHECK_ALARM")
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), getConfiguredSecondsInMillis(), pendingIntent)
    }

    private fun getConfiguredSecondsInMillis() : Long {
        var seconds = try {
            val preferences = PreferenceManager.getDefaultSharedPreferences(App.context)
            Integer.parseInt(preferences.getString("disruption_check_interval", "300"))
        }
        catch (e : NumberFormatException) {
            Log.e(TAG, "Unable to read check interval from preferences, defaulting to 300 seconds.", e)
            300
        }

        return (1000 * seconds).toLong()
    }

}