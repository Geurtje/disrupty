package com.geuso.disrupty.notification

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.geuso.disrupty.disruption.DisruptionService


class DisruptionCheckJobService : JobService() {

    companion object {
        private val TAG = DisruptionCheckJobService::class.qualifiedName
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "DisruptionCheckJob started")

        Thread(Runnable {
            DisruptionService(applicationContext).notifyDisruptedSubscriptions()
            Log.d(TAG, "DisruptionCheckJob finished")
            jobFinished(params, false)
        }).start()

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "DisruptionCheckJob cancelled before completion")
        return true
    }

}