package com.geuso.disrupty.disruption

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.geuso.disrupty.App
import com.geuso.disrupty.R
import com.geuso.disrupty.db.AppDatabase
import com.geuso.disrupty.disruption.model.DisruptionCheck
import com.geuso.disrupty.notification.DisruptionNotificationService
import com.geuso.disrupty.ns.NsRestClient
import com.geuso.disrupty.ns.traveloption.TravelOptionXmlParser
import com.geuso.disrupty.subscription.model.Status
import com.geuso.disrupty.subscription.model.Subscription
import com.geuso.disrupty.subscription.model.SubscriptionDao
import com.geuso.disrupty.subscription.model.TimeConverter
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import java.util.*
import kotlin.collections.ArrayList

object DisruptionService {

    private val TAG = DisruptionService::class.qualifiedName
    private val subscriptionDao : SubscriptionDao = AppDatabase.INSTANCE.subscriptionDao()


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
            val alertMessage = App.context.resources.getString(R.string.disruption_check_no_network)
            Log.e(TAG, alertMessage)
        }

    }

    private fun checkSubscriptionsAndNotifyIfDisrupted(subscriptions: List<Subscription>) {
        for (subscription in subscriptions) {
            val params = NsRestClient.paramsForTravelOptions(subscription.stationFrom, subscription.stationTo)
            NsRestClient.get(NsRestClient.PATH_TRAVEL_OPTIONS, params, object: TextHttpResponseHandler() {

                override fun getUseSynchronousMode(): Boolean {
                    return false
                }

                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: String?) {
                    val travelOptions = TravelOptionXmlParser().parse(responseBody!!.byteInputStream())

                    val disruptionCheckResult = DisruptionEvaluator.getDisruptionCheckResultFromTravelOptions(travelOptions)

                    val isDisrupted = disruptionCheckResult.isDisrupted
                    val disruptionMessage =  resolveDisruptionMessage(disruptionCheckResult)

                    val disruptionCheck = DisruptionCheck(subscription.id, Calendar.getInstance().time, isDisrupted, disruptionMessage, responseBody)
                    val newSubscriptionStatus = if (isDisrupted) Status.NOT_OK else Status.OK

                    if (shouldNotifyStatusChange(subscription, newSubscriptionStatus)) {
                        val disruptionStatus = disruptionCheckResult.disruptionStatus
                        Log.i(TAG, "Sending notification for subscription: $subscription, new status: $newSubscriptionStatus, disruption status: $disruptionStatus")

                        DisruptionNotificationService.sendNotification(subscription, disruptionCheck, newSubscriptionStatus, disruptionStatus)
                    }

                    saveDisruptionCheck(disruptionCheck)
                    saveSubscriptionIfNecessary(subscription, newSubscriptionStatus)
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: String?, error: Throwable?) {
                    Log.e(TAG, "Failure: $statusCode, body: $responseBody")
                    val disruptionCheck = DisruptionCheck(subscription.id, Calendar.getInstance().time, false, null, "$error\n\n\n$responseBody", false)
                    saveDisruptionCheck(disruptionCheck)
                    saveSubscriptionStatus(subscription, Status.UNKNOWN)
                }
            })
        }
    }

    private fun resolveDisruptionMessage(disruptionCheckResult: DisruptionEvaluator.DisruptionCheckResult): String {
        if (disruptionCheckResult.departureDelay != null) {
            return disruptionCheckResult.departureDelay
        }
        else if (disruptionCheckResult.message != null) {
            return disruptionCheckResult.message
        }

        return "";
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


    private fun shouldNotifyStatusChange(subscription: Subscription, newStatus: Status): Boolean {
        if (subscription.status == newStatus) {
            return false
        }

        if (subscription.status == Status.UNKNOWN && newStatus == Status.OK) {
            return false
        }

        return true
    }

    private fun saveSubscriptionIfNecessary(subscription: Subscription, status: Status) {
        if (subscription.status != status) {
            saveSubscriptionStatus(subscription, status)
        }
    }

    private fun saveDisruptionCheck(disruptionCheck: DisruptionCheck) {
        val disruptionCheckDao = AppDatabase.INSTANCE.disruptionCheckDao()
        disruptionCheckDao.upsertDisruptionCheck(disruptionCheck)
    }

    private fun saveSubscriptionStatus(subscription: Subscription, status: Status) {
        subscription.status = status
        val subscriptionDao = AppDatabase.INSTANCE.subscriptionDao()
        subscriptionDao.upsertSubscription(subscription)
    }

    private fun isNetworkConnectionAvailable() : Boolean {
        val connectivityManager = App.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}