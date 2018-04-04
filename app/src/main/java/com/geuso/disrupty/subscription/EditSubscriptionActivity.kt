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
import com.geuso.disrupty.model.Subscription
import com.geuso.disrupty.util.ButtonTimePicketDialog
import com.geuso.disrupty.util.extractHourAndMinuteFromText
import com.geuso.disrupty.util.formatTime
import kotlinx.android.synthetic.main.activity_edit_subscription.*


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

    private var subscriptionId : Long? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        onCreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreate()
    }

    fun onCreate(){
        setContentView(LAYOUT_ID)

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
                .setPositiveButton(R.string.confirm_positive, {
                    _, _ ->
                    val dao = AppDatabase.INSTANCE.subscriptionDao()
                    val rowsDeleted = dao.deleteSubscriptionById(this.subscriptionId!!)

                    if (rowsDeleted != 1) {
                        Toast.makeText(this, R.string.delete_failure, Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                })
                .setNegativeButton(R.string.confirm_negative, {
                    dialogInterface, _ -> dialogInterface.cancel()
                })
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

        val mondayEnabled: Boolean = input_day_monday.isEnabled
        val tuesdayEnabled: Boolean = input_day_tuesday.isEnabled
        val wednesdayEnabled: Boolean = input_day_wednesday.isEnabled
        val thursdayEnabled: Boolean = input_day_thursday.isEnabled
        val fridayEnabled: Boolean = input_day_friday.isEnabled
        val saturdayEnabled: Boolean = input_day_saturday.isEnabled
        val sundayEnabled: Boolean = input_day_sunday.isEnabled



        val sub = Subscription(stationFrom, stationTo,
                timeFromHour, timeFromMinute,
                timeToHour, timeToMinute,
                mondayEnabled, tuesdayEnabled, wednesdayEnabled, thursdayEnabled, fridayEnabled, saturdayEnabled, sundayEnabled
        )

        if (this.subscriptionId != null) {
            sub.id = this.subscriptionId!!
        }


        val dao = AppDatabase.INSTANCE.subscriptionDao()
        val rowsUpdated = dao.upsertSubscription(sub)

        Log.i(TAG, "Saved subscription with ID: $rowsUpdated.")
        Toast.makeText(applicationContext, "Saved subscription with ID: $rowsUpdated.", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun populateFormWithSubscription(subscriptionId : Long) {


        val subscription = AppDatabase.INSTANCE.subscriptionDao().getSubscriptionById(subscriptionId)

        Log.i(TAG, "Loading subscription $subscriptionId: $subscription")

        input_station_from.setText(subscription.stationFrom)
        input_station_to.setText(subscription.stationTo)

        button_time_from.text = formatTime(subscription.timeFromHour, subscription.timeFromMinute)
        button_time_to.text = formatTime(subscription.timeToHour, subscription.timeToMinute)


        input_day_monday.isEnabled = subscription.monday
        input_day_tuesday.isEnabled = subscription.tuesday
        input_day_wednesday.isEnabled = subscription.wednesday
        input_day_thursday.isEnabled = subscription.thursday
        input_day_friday.isEnabled = subscription.friday
        input_day_saturday.isEnabled = subscription.saturday
        input_day_sunday.isEnabled = subscription.sunday

    }

}