package com.geuso.disrupty.disruption

import com.geuso.disrupty.db.AppDatabase
import com.geuso.disrupty.model.db.Subscription
import com.geuso.disrupty.model.db.SubscriptionDao
import com.geuso.disrupty.model.db.TimeConverter
import java.util.*
import kotlin.collections.ArrayList

object DisruptionService {

    private val TAG = DisruptionService::class.qualifiedName
    private val subscriptionDao : SubscriptionDao = AppDatabase.INSTANCE.subscriptionDao()


    /**
     * Will check disruptions for all subscriptions .
     * @return a list of subscriptions which currently have a disruption
     */
    fun checkSubscriptionsDisrupted() : List<Subscription> {
        val subscriptionsToCheck = getSubscriptionsToCheck()
        return getDisruptedSubscriptions(subscriptionsToCheck)
    }

    private fun getDisruptedSubscriptions(subscriptions: List<Subscription>) : List<Subscription> {
        TODO("Implementation pending")
        return Collections.emptyList()
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
}