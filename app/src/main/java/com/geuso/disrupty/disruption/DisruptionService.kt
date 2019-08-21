package com.geuso.disrupty.disruption

import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import com.geuso.disrupty.R
import com.geuso.disrupty.db.AppDatabase
import com.geuso.disrupty.disruption.model.DisruptionCheck
import com.geuso.disrupty.disruption.model.DisruptionCheckDao
import com.geuso.disrupty.notification.DisruptionNotificationService
import com.geuso.disrupty.ns.NsPublicTravelRestClient
import com.geuso.disrupty.ns.traveloption.TravelOptionJsonParser
import com.geuso.disrupty.subscription.model.Status
import com.geuso.disrupty.subscription.model.Subscription
import com.geuso.disrupty.subscription.model.SubscriptionDao
import com.geuso.disrupty.subscription.model.TimeConverter
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList


class DisruptionService(val context: Context) {

    private val subscriptionDao: SubscriptionDao = AppDatabase.getInstance(context).subscriptionDao()

    companion object {
        private val TAG = DisruptionService::class.qualifiedName

    }


    /**
     * For all currently active subscriptions, check if there are any disrupted travel options.
     * If so, send a notification.
     */
    fun notifyDisruptedSubscriptions() {
        if (isNetworkConnectionAvailable()) {
            val subscriptionsToCheck = getSubscriptionsToCheck()
            checkSubscriptionsAndNotifyIfDisrupted(subscriptionsToCheck)
        }
        else {
            val alertMessage = context.resources.getString(R.string.disruption_check_no_network)
            Log.e(TAG, alertMessage)
        }

    }

    private fun checkSubscriptionsAndNotifyIfDisrupted(subscriptions: List<Subscription>) {
        if (subscriptions.isNotEmpty()) {
            val mainHandler = Handler(Looper.getMainLooper())
            for (subscription in subscriptions) {
                mainHandler.post(RunnableTripsJsonHttpResponseHandler(context, subscription))
            }
        }
    }


    /*
     * Wasn't able to get date/time comparisons to work correctly in with SQLite, so I just
     * resorted to filtering the subscriptions manually.
     * TODO redo the subscription model to store days in a separate table so that the day selection
     * can be moved to the SubcriptionDao.
     */
    private fun getSubscriptionsToCheck() : List<Subscription> {
        val allSubscriptions = subscriptionDao.getAllSubscriptions()
        return filterSubscriptionsActiveToday(allSubscriptions)
    }

    private fun filterSubscriptionsActiveToday(subscriptions: List<Subscription>) : List<Subscription> {
        val currentTime = Calendar.getInstance()

        val subscriptionsActiveNow = ArrayList<Subscription>()
        val dayOfWeek = currentTime.get(Calendar.DAY_OF_WEEK)
        val currentDate = TimeConverter.INSTANCE.fromTimeString("${currentTime.get(Calendar.HOUR_OF_DAY)}:${currentTime.get(Calendar.MINUTE)}")
        for (subscription in subscriptions) {
            if (isSubscriptionActiveOnDay(subscription, dayOfWeek) &&
                    isSubscriptionInTimeFrame(subscription, currentDate)) {
                subscriptionsActiveNow.add(subscription)
            }
        }

        return subscriptionsActiveNow
    }

    private fun isSubscriptionActiveOnDay(subscription: Subscription, dayOfWeek: Int) = when (dayOfWeek) {
        Calendar.SUNDAY -> subscription.sunday
        Calendar.MONDAY -> subscription.monday
        Calendar.TUESDAY -> subscription.tuesday
        Calendar.WEDNESDAY -> subscription.wednesday
        Calendar.THURSDAY -> subscription.thursday
        Calendar.FRIDAY -> subscription.friday
        Calendar.SATURDAY -> subscription.saturday
        else -> false
    }

    private fun isSubscriptionInTimeFrame(subscription: Subscription, currentTime: Date): Boolean {
        return currentTime.after(subscription.timeFrom) && currentTime.before(subscription.timeTo)
    }

    private fun isNetworkConnectionAvailable() : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


    private class RunnableTripsJsonHttpResponseHandler(val context: Context,
                                                    val subscription: Subscription) : JsonHttpResponseHandler(), Runnable {
        override fun run() {
            val nsRestClient = NsPublicTravelRestClient(context)
            val params = nsRestClient.paramsForTravelOptions(subscription.stationFrom, subscription.stationTo)
            nsRestClient.get(NsPublicTravelRestClient.PATH_TRIPS, params, this)
        }

        private val subscriptionDao: SubscriptionDao = AppDatabase.getInstance(context).subscriptionDao()
        private val disruptionCheckDao: DisruptionCheckDao = AppDatabase.getInstance(context).disruptionCheckDao()

        override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
            super.onSuccess(statusCode, headers, response)
            if (response != null) {
                val travelOptions = TravelOptionJsonParser().parseTravelOptions(response)

                val disruptionEvaluator = DisruptionEvaluator(context, PreferenceManager.getDefaultSharedPreferences(context))
                val disruptionCheckResult = disruptionEvaluator.getDisruptionCheckResultFromTravelOptions(travelOptions)

                val isDisrupted = disruptionCheckResult.isDisrupted
                val disruptionMessage =  resolveDisruptionMessage(disruptionCheckResult)

                val disruptionCheck = DisruptionCheck(subscription.id, Instant.now(), isDisrupted, disruptionMessage, response.toString(2))
                val newSubscriptionStatus = if (isDisrupted) Status.NOT_OK else Status.OK

                val oldSubscriptionStatus = subscription.status

                insertDisruptionCheckAndAssignId(disruptionCheck)
                updateSubscriptionIfNecessary(subscription, newSubscriptionStatus)

                if (shouldNotifyStatusChange(oldSubscriptionStatus, newSubscriptionStatus)) {
                    val disruptionStatus = disruptionCheckResult.disruptionStatus
                    Log.i(TAG, "Sending notification for subscription: $subscription, new status: $newSubscriptionStatus, disruption status: $disruptionStatus")

                    DisruptionNotificationService.sendNotification(context, subscription, disruptionCheck, newSubscriptionStatus, disruptionStatus)
                }
            }
        }

        override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: String?, throwable: Throwable?) {
            Log.e(TAG, "Failure: $statusCode, body: $responseBody", throwable)
            handleFailure(subscription, responseBody ?: "")
        }

        override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
            Log.e(TAG, "Failure: $statusCode, body: $errorResponse", throwable)
            handleFailure(subscription, errorResponse?.toString(2) ?: "")

        }

        private fun handleFailure(subscription: Subscription, responseBody: String) {
            val disruptionCheck = DisruptionCheck(subscription.id, Instant.now(), false, null, responseBody, false)
            insertDisruptionCheckAndAssignId(disruptionCheck)
            updateSubscriptionIfNecessary(subscription, Status.UNKNOWN)
        }

        private fun resolveDisruptionMessage(disruptionCheckResult: DisruptionEvaluator.DisruptionCheckResult): String {
            if (disruptionCheckResult.departureDelay != null) {
                return disruptionCheckResult.departureDelay
            }
            else if (disruptionCheckResult.message != null) {
                return disruptionCheckResult.message
            }

            return ""
        }

        private fun shouldNotifyStatusChange(oldStatus: Status, newStatus: Status): Boolean {
            if (oldStatus == newStatus) {
                return false
            }

            if (oldStatus == Status.UNKNOWN && newStatus == Status.OK) {
                return false
            }

            return true
        }

        private fun updateSubscriptionIfNecessary(subscription: Subscription, status: Status) {
            if (subscription.status != status) {
                subscription.status = status
                subscriptionDao.upsertSubscription(subscription)
            }
        }

        private fun insertDisruptionCheckAndAssignId(disruptionCheck: DisruptionCheck) {
            val disruptionId = disruptionCheckDao.upsertDisruptionCheck(disruptionCheck)
            disruptionCheck.id = disruptionId
        }

    }

}