package com.geuso.disrupty.disruption.log

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.geuso.disrupty.R
import com.geuso.disrupty.db.AppDatabase
import com.geuso.disrupty.db.InstantConverter
import com.geuso.disrupty.disruption.DisruptionEvaluator
import com.geuso.disrupty.disruption.model.DisruptionCheck
import com.geuso.disrupty.ns.traveloption.TravelOptionXmlParser
import kotlinx.android.synthetic.main.disruption_check_detail_result_view.*
import kotlinx.android.synthetic.main.disruption_check_detail_view.*
import org.xmlpull.v1.XmlPullParserException
import java.io.ByteArrayInputStream

class DisruptionCheckDetailActivity : AppCompatActivity() {

    companion object {
        private val TAG = DisruptionCheckDetailActivity::class.qualifiedName
        private const val LAYOUT_ID: Int = R.layout.disruption_check_detail_view
        private const val EXTRA_DISRUPTION_CHECK_ID = "DISRUPTION_CHECK"

        fun start(context: Context, disruptionCheck: DisruptionCheck) {
            val intent = intent(context, disruptionCheck)
            context.startActivity(intent)
        }

        fun intent(context: Context, disruptionCheck: DisruptionCheck) : Intent {
            val intent = Intent(context, DisruptionCheckDetailActivity::class.java)
            intent.putExtra(EXTRA_DISRUPTION_CHECK_ID, disruptionCheck.id)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!intent.extras.containsKey(EXTRA_DISRUPTION_CHECK_ID)) {
            // If this activity was started without a disruptionCheck id then we can't do anything
            Toast.makeText(this, applicationContext.resources.getString(R.string.disruption_check_detail_missing_id), Toast.LENGTH_SHORT).show()
            finish()
        }

        val disruptionCheckId = intent.extras.getLong(EXTRA_DISRUPTION_CHECK_ID)
        val disruptionCheck = AppDatabase.getInstance(applicationContext).disruptionCheckDao().getDisruptionCheckById(disruptionCheckId)

        if (disruptionCheck == null) {
            Log.w(TAG, "Failed to show details for DisruptionCheck id '$disruptionCheckId' because it does not exist.")
            finish()
        }

        val subscription = AppDatabase.getInstance(applicationContext).subscriptionDao().getSubscriptionById(disruptionCheck!!.subscriptionId)

        setContentView(LAYOUT_ID)

        if (subscription != null) {
            dc_detail_station_from.text = subscription.stationFrom
            dc_detail_station_to.text = subscription.stationTo
        }
        else {
            dc_detail_station_from.text = "unknown"
            dc_detail_station_to.text = "unknown"
        }

        dc_detail_timestamp.text = InstantConverter().format(disruptionCheck.timestamp)
        dc_detail_status.text = disruptionCheck.success.toString()

        dc_detail_response.text = disruptionCheck.response

        populateResponseResults(disruptionCheck)
    }

    private fun populateResponseResults(disruptionCheck: DisruptionCheck) {
        val travelOptions = try {
            TravelOptionXmlParser().parse(ByteArrayInputStream(disruptionCheck.response.toByteArray(Charsets.UTF_8)))
        }
        catch (e : XmlPullParserException) {
            // Don't provide a summary in case of a malformed xml response
            return
        }

        val disruptionEvaluator = DisruptionEvaluator(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

        val (isDisrupted, message, disruptionStatus, departureDelay) = disruptionEvaluator.getDisruptionCheckResultFromTravelOptions(travelOptions)

        val disruptionImage = if (isDisrupted) R.drawable.ic_subscription_status_not_ok
            else R.drawable.ic_subscription_status_ok

        dc_result_status_icon.setImageDrawable(ContextCompat.getDrawable(baseContext, disruptionImage))

        setTextOrGreyedOutDefault(baseContext, dc_result_message, message, R.string.disruption_check_result_message, R.string.disruption_check_default_result_message)
        setTextOrGreyedOutDefault(baseContext, dc_result_departure_delay, departureDelay, R.string.disruption_check_result_delay, R.string.disruption_check_default_result_delay)

        dc_result_travel_option_status.text = disruptionStatus.key
    }

    /**
     * Populates a given TextView with a formatted text message. If the message is not populated
     * then a default message can be specified. If the message is missing then a grayed out
     * colour will be applied to the TextView.
     *
     * @param context
     * @param view The TextView to populate with the text
     * @param text The text message to be displayed
     * @param textResource The main resource in which the text message should be placed, [text] or [defaultStringResource] will be used to format this string.
     * @param defaultStringResource The fallback text resource to use if [text] is empty
     */
    private fun setTextOrGreyedOutDefault(context: Context, view: TextView, text: String?, textResource: Int, defaultStringResource: Int) {
        val isValueMissing = text.isNullOrBlank()
        val value = if (isValueMissing) context.resources.getString(defaultStringResource)
            else text

        view.text = context.resources.getString(textResource, value)
        if (isValueMissing) {
            view.setTextColor(ContextCompat.getColor(context, R.color.text_grayed_out))
        }
    }


}
