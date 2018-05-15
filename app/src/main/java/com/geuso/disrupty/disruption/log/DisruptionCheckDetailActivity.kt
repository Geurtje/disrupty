package com.geuso.disrupty.disruption.log

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.geuso.disrupty.R
import com.geuso.disrupty.db.AppDatabase
import com.geuso.disrupty.disruption.model.DisruptionCheck
import com.geuso.disrupty.subscription.model.Subscription
import kotlinx.android.synthetic.main.disruption_check_detail_view.*

class DisruptionCheckDetailActivity : AppCompatActivity() {

    companion object {
        private val TAG = DisruptionCheckDetailActivity::class.qualifiedName
        private const val LAYOUT_ID: Int = R.layout.disruption_check_detail_view
        private const val EXTRA_DISRUPTION_CHECK_ID = "DISRUPTION_CHECK"

        fun start(context: Context, disruptionCheck: DisruptionCheck) {
            val intent = Intent(context, DisruptionCheckDetailActivity::class.java)
            intent.putExtra(EXTRA_DISRUPTION_CHECK_ID, disruptionCheck.id)
            context.startActivity(intent)
        }
    }

    private lateinit var disruptionCheck : DisruptionCheck
    private lateinit var subscription : Subscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!intent.extras.containsKey(EXTRA_DISRUPTION_CHECK_ID)) {
            // If this activity was started without a disruptionCheck id then we can't do anything
            Toast.makeText(this, applicationContext.resources.getString(R.string.disruption_check_detail_missing_id), Toast.LENGTH_SHORT).show()
            finish()
        }

        val disruptionCheckId = intent.extras.getLong(EXTRA_DISRUPTION_CHECK_ID)
        disruptionCheck = AppDatabase.INSTANCE.disruptionCheckDao().getDisruptionCheckById(disruptionCheckId)
        subscription = AppDatabase.INSTANCE.subscriptionDao().getSubscriptionById(disruptionCheck.subscriptionId)

        setContentView(LAYOUT_ID)

        dc_detail_station_from.text = subscription.stationFrom
        dc_detail_station_to.text = subscription.stationTo

        dc_detail_timestamp.text = disruptionCheck.checkTime.toString()
        dc_detail_status.text = disruptionCheck.success.toString()

        dc_detail_response.text = disruptionCheck.response

    }


}
