package com.geuso.disrupty.disruption

import android.util.Log
import com.geuso.disrupty.db.AppDatabase
import com.geuso.disrupty.disruption.model.DisruptionCheck
import com.geuso.disrupty.notification.DisruptionNotificationService
import com.geuso.disrupty.ns.NsRestClient
import com.geuso.disrupty.ns.traveloption.DisruptionStatus
import com.geuso.disrupty.ns.traveloption.TravelOption
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
    private val DISRUPTED_STATUSES : List<DisruptionStatus> = listOf(DisruptionStatus.DELAYED, DisruptionStatus.NOT_POSSIBLE)

    /**
     * For all currently active subscriptions, check if there are any disrupted travel options.
     * If so, send a notification.
     */
    fun notifyDisruptedSubscriptions() {
        val subscriptionsToCheck = getSubscriptionsToCheck()
        checkSubscriptionsAndNotifyIfDisrupted(subscriptionsToCheck)

    }

    private fun checkSubscriptionsAndNotifyIfDisrupted(subscriptions: List<Subscription>) {
        for (subscription in subscriptions) {
            val params = NsRestClient.paramsForTravelOptions(subscription.stationFrom, subscription.stationTo)
            NsRestClient.get(NsRestClient.PATH_TRAVEL_OPTIONS, params, object: TextHttpResponseHandler() {

                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: String?) {
                    val travelOptions = TravelOptionXmlParser().parse(responseBody!!.byteInputStream())

                    val isDisruptedPair = getDisruptedPairFromTravelOptions(travelOptions)
                    val isDisrupted = isDisruptedPair.first
                    val disruptionMessage =  isDisruptedPair.second

                    val disruptionCheck = DisruptionCheck(subscription.id, Calendar.getInstance().time, isDisrupted, disruptionMessage)
                    val newSubscriptionStatus = if (isDisrupted) Status.NOT_OK else Status.OK

                    if (shouldNotifyStatusChange(subscription, newSubscriptionStatus)) {
                        Log.i(TAG, "Sending notification for subscription: $subscription, new status: $newSubscriptionStatus")
                        DisruptionNotificationService.sendNotification(subscription, disruptionCheck, newSubscriptionStatus)
                    }

                    saveDisruptionCheck(disruptionCheck)
                    saveSubscriptionIfNecessary(subscription, newSubscriptionStatus)
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: String?, error: Throwable?) {
                    Log.e(TAG, "Failure: $statusCode, body: $responseBody")
                    val disruptionCheck = DisruptionCheck(subscription.id, Calendar.getInstance().time, false, null, false)
                    saveDisruptionCheck(disruptionCheck)
                    saveSubscriptionStatus(subscription, Status.UNKNOWN)
                }
            })
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
        0 -> subscription.sunday
        1 -> subscription.monday
        2 -> subscription.tuesday
        3 -> subscription.wednesday
        4 -> subscription.thursday
        5 -> subscription.friday
        6 -> subscription.saturday
        else -> false
    }

    private fun isSubscriptionInTimeFrame(subscription: Subscription, currentTime: Date): Boolean {
        return currentTime.after(subscription.timeFrom) && currentTime.before(subscription.timeTo)
    }

    /**
     * Returns a Pair, the first entry is a boolean indicating if any of the traveloptions in
     * this list have a disruption. The second entry is the notification text part of that
     * traveloption.
     */
    private fun getDisruptedPairFromTravelOptions(travelOptions: List<TravelOption>) : Pair<Boolean, String?> {
        for (travelOption in travelOptions) {
            if (DISRUPTED_STATUSES.contains(travelOption.disruptionStatus)) {
                return Pair(true, travelOption.notification?.text)
            }
        }
        return Pair(false, null)
    }

    private fun shouldNotifyStatusChange(subscription: Subscription, status: Status): Boolean {
        if (subscription.status == status) {
            return false
        }

        if (subscription.status == Status.UNKNOWN && status == Status.OK) {
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


}