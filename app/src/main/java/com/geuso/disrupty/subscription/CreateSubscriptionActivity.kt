package com.geuso.disrupty.subscription

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.geuso.disrupty.R
import com.geuso.disrupty.db.AppDatabase
import com.geuso.disrupty.model.Subscription
import com.geuso.disrupty.util.ButtonTimePicketDialog
import com.geuso.disrupty.util.extractHourAndMinuteFromText
import kotlinx.android.synthetic.main.activity_create_subscription.*


class CreateSubscriptionActivity : AppCompatActivity(), View.OnClickListener {


    private val TAG = CreateSubscriptionActivity::class.qualifiedName
    private val layoutId : Int = R.layout.activity_create_subscription


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        onCreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreate()
    }

    fun onCreate(){
        setContentView(layoutId)


        button_time_from.setOnClickListener(this)
        button_time_to.setOnClickListener(this)
        button_save.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if (v == null){
            return
        }

        when (v) {
            button_time_from -> ButtonTimePicketDialog(this, true, button_time_from).show()
            button_time_to -> ButtonTimePicketDialog(this, true, button_time_to).show()
            button_save -> saveSubscription()
        }
    }

    fun saveSubscription() {
        val stationFrom: String = input_station_from.text.toString()
        val stationTo: String  = input_station_to.text.toString()

        val timeFrom: Pair<Int, Int> = extractHourAndMinuteFromText(button_time_from.text)
        val timeFromHour: Int = timeFrom.first
        val timeFromMinute: Int = timeFrom.second

        val timeTo: Pair<Int, Int> = extractHourAndMinuteFromText(button_time_from.text)
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

        val dao = AppDatabase.INSTANCE.subscriptionDao()
        val rowsUpdated = dao.upsertSubscription(sub)

        Log.i(TAG, "Saved subscription with ID: $rowsUpdated.")

    }

}