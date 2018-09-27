package com.geuso.disrupty.notification

import android.app.job.JobInfo
import android.content.ComponentName
import android.content.Context
import android.util.Log
import com.geuso.disrupty.App

object DisruptionCheckJobScheduler {

    private val TAG = DisruptionCheckJobScheduler::class.qualifiedName
    private const val JOB_ID = 1001
    private val SCHEDULER =  App.context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as android.app.job.JobScheduler

    fun scheduleDisruptionCheckJobIfRequired() {
        // Check if the job is scheduled by "getting" it
        val pendingJob = SCHEDULER.getPendingJob(JOB_ID)
        if (pendingJob == null) {
            scheduleJob(App.context)
        }

    }

    private fun scheduleJob(context: Context) {
        val componentName = ComponentName(context, DisruptionCheckJobService::class.java)
        val info = JobInfo.Builder(JOB_ID, componentName)
                .setRequiresDeviceIdle(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000, 5 * 60 * 1000)
                .build()

        val resultCode = SCHEDULER.schedule(info)
        if (resultCode == android.app.job.JobScheduler.RESULT_SUCCESS) {
            Log.i(TAG, "Job $JOB_ID scheduled")
        } else {
            Log.i(TAG, "Job $JOB_ID scheduling failed")
        }
    }

    private fun unscheduleJob(context: Context) {
        SCHEDULER.cancel(JOB_ID)
        Log.i(TAG, "Job $JOB_ID unscheduled")
    }

}