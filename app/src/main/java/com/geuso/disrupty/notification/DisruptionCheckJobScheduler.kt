package com.geuso.disrupty.notification

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.util.Log

class DisruptionCheckJobScheduler(val context: Context) {

    companion object {
        private val TAG = DisruptionCheckJobScheduler::class.qualifiedName
        private const val JOB_ID = 1001
    }

    fun scheduleDisruptionCheckJobIfRequired() {
        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        // Check if the job is scheduled by "getting" it
        val pendingJob = scheduler.getPendingJob(JOB_ID)
        if (pendingJob == null) {
            scheduleJob(context, scheduler)
        }

    }

    private fun scheduleJob(context: Context, jobScheduler: JobScheduler) {
        val componentName = ComponentName(context, DisruptionCheckJobService::class.java)
        val info = JobInfo.Builder(JOB_ID, componentName)
                .setRequiresDeviceIdle(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000, 5 * 60 * 1000)
                .build()

        val resultCode = jobScheduler.schedule(info)
        if (resultCode == android.app.job.JobScheduler.RESULT_SUCCESS) {
            Log.i(TAG, "Job $JOB_ID scheduled")
        } else {
            Log.i(TAG, "Job $JOB_ID scheduling failed")
        }
    }

    private fun unscheduleJob(jobScheduler: JobScheduler) {
        jobScheduler.cancel(JOB_ID)
        Log.i(TAG, "Job $JOB_ID unscheduled")
    }

}