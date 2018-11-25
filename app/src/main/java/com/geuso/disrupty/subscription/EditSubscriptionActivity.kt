package com.geuso.disrupty.subscription

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.geuso.disrupty.R
import com.geuso.disrupty.db.AppDatabase
import com.geuso.disrupty.subscription.EditSubscriptionActivity.Companion.EXTRA_SUBSCRIPTION_ID
import com.geuso.disrupty.subscription.model.Status
import com.geuso.disrupty.subscription.model.Subscription
import com.geuso.disrupty.subscription.model.SubscriptionDao
import com.geuso.disrupty.subscription.model.TimeConverter
import com.geuso.disrupty.util.ButtonTimePicketDialog
import com.geuso.disrupty.util.extractHourAndMinuteFromText
import kotlinx.android.synthetic.main.activity_edit_subscription.*


/**
 * Activity for both creating, editing and deleting a subscription.
 * If the intent that is used to start this activity has an extra called [EXTRA_SUBSCRIPTION_ID],
 * this subscription ID will be loaded from the database. This flag is also the switch for showing
 * a delete button.
 *
 * Stuff to improve:
 * - Proper form validation: stations can't be empty, time range can't be negative.
 * - Error handling in case of an unknown, to be verified in the SubscriptionDao.
 */
class EditSubscriptionActivity : AppCompatActivity(), View.OnClickListener {

    companion object {

        private val TAG = EditSubscriptionActivity::class.qualifiedName
        private const val LAYOUT_ID: Int = R.layout.activity_edit_subscription
        private const val EXTRA_SUBSCRIPTION_ID = "SUBSCRIPTION_ID"

        fun start(context: Context) {
            context.startActivity(Intent(context, EditSubscriptionActivity::class.java))
        }

        fun start(context: Context, subscriptionId: Long) {
            val intent = Intent(context, EditSubscriptionActivity::class.java)
            intent.putExtra(EXTRA_SUBSCRIPTION_ID, subscriptionId)
            context.startActivity(intent)
        }

    }

    private lateinit var subscriptionDao: SubscriptionDao
    private var subscriptionId : Long? = null
    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        onCreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreate()
    }

    private fun onCreate(){
        setContentView(LAYOUT_ID)

        subscriptionDao = AppDatabase.getInstance(this).subscriptionDao()

        if (intent.extras != null && intent.extras.containsKey(EXTRA_SUBSCRIPTION_ID)) {
            this.subscriptionId = intent.extras.getLong(EXTRA_SUBSCRIPTION_ID)
            populateFormWithSubscription(this.subscriptionId!!)
        }

        if (subscriptionId == null) {
            button_delete.visibility = View.GONE
        }

        input_station_from.setOnClickListener(this)
        input_station_to.setOnClickListener(this)
        button_time_from.setOnClickListener(this)
        button_time_to.setOnClickListener(this)
        button_save.setOnClickListener(this)
        button_delete.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if (v == null){
            return
        }

        when (v) {
            input_station_from -> StationSelectionDialog(this, input_station_from).show()
            input_station_to -> StationSelectionDialog(this, input_station_to).show()
            button_time_from -> ButtonTimePicketDialog(this, true, button_time_from).show()
            button_time_to -> ButtonTimePicketDialog(this, true, button_time_to).show()
            button_save -> saveSubscription()
            button_delete -> deleteSubscription()
        }
    }

    private fun deleteSubscription() {
        if (this.subscriptionId == null) {
            return
        }

        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage(R.string.delete_confirmation)
                .setPositiveButton(R.string.confirm_positive) { _, _ ->
                    val dao = AppDatabase.getInstance(applicationContext).subscriptionDao()
                    val rowsDeleted = dao.deleteSubscriptionById(this.subscriptionId!!)

                    if (rowsDeleted != 1) {
                        Toast.makeText(this, R.string.delete_failure, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .setNegativeButton(R.string.confirm_negative) {
                    dialogInterface, _ -> dialogInterface.cancel()
                }
                .show()
    }

    private fun saveSubscription() {
        val stationFrom: String = input_station_from.text.toString()
        val stationTo: String  = input_station_to.text.toString()

        val timeFrom: Pair<Int, Int> = extractHourAndMinuteFromText(button_time_from.text)
        val timeFromHour: Int = timeFrom.first
        val timeFromMinute: Int = timeFrom.second

        val timeTo: Pair<Int, Int> = extractHourAndMinuteFromText(button_time_to.text)
        val timeToHour: Int = timeTo.first
        val timeToMinute: Int = timeTo.second

        val mondayEnabled: Boolean = input_day_monday.isChecked
        val tuesdayEnabled: Boolean = input_day_tuesday.isChecked
        val wednesdayEnabled: Boolean = input_day_wednesday.isChecked
        val thursdayEnabled: Boolean = input_day_thursday.isChecked
        val fridayEnabled: Boolean = input_day_friday.isChecked
        val saturdayEnabled: Boolean = input_day_saturday.isChecked
        val sundayEnabled: Boolean = input_day_sunday.isChecked

        val status: Status = if (subscription != null) subscription!!.status else Status.UNKNOWN

        val sub = Subscription(stationFrom, stationTo,
                TimeConverter.INSTANCE.fromTimeString("$timeFromHour:$timeFromMinute"),
                TimeConverter.INSTANCE.fromTimeString("$timeToHour:$timeToMinute"),
                mondayEnabled, tuesdayEnabled, wednesdayEnabled, thursdayEnabled, fridayEnabled, saturdayEnabled, sundayEnabled,
                status
        )

        if (this.subscriptionId != null) {
            sub.id = this.subscriptionId!!
        }


        val subscriptionId = subscriptionDao.upsertSubscription(sub)

        Log.i(TAG, "Saved subscription with ID: $subscriptionId.")
        Toast.makeText(applicationContext, "Saved subscription with ID: $subscriptionId.", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun populateFormWithSubscription(subscriptionId : Long) {
        val subscription = subscriptionDao.getSubscriptionById(subscriptionId)

        if (subscription != null) {
            Log.i(TAG, "Loading subscription $subscriptionId: $subscription")

            input_station_from.setText(subscription.stationFrom)
            input_station_to.setText(subscription.stationTo)

            button_time_from.text = TimeConverter.INSTANCE.dateToTime(subscription.timeFrom)
            button_time_to.text = TimeConverter.INSTANCE.dateToTime(subscription.timeTo)

            input_day_monday.isChecked = subscription.monday
            input_day_tuesday.isChecked = subscription.tuesday
            input_day_wednesday.isChecked = subscription.wednesday
            input_day_thursday.isChecked = subscription.thursday
            input_day_friday.isChecked = subscription.friday
            input_day_saturday.isChecked = subscription.saturday
            input_day_sunday.isChecked = subscription.sunday

            this.subscription = subscription
        }
    }

}