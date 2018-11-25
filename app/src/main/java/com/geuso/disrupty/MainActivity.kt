package com.geuso.disrupty

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.geuso.disrupty.disruption.log.DisruptionCheckActivity
import com.geuso.disrupty.notification.DisruptionCheckJobScheduler
import com.geuso.disrupty.settings.DisruptyPreferenceActivity
import com.geuso.disrupty.subscription.EditSubscriptionActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        create_subscription_floating_button.setOnClickListener {
            EditSubscriptionActivity.start(this)
        }

        // Is this really necessary? I'm not sure whats a good way to force schedule jobs
        DisruptionCheckJobScheduler(applicationContext).scheduleDisruptionCheckJobIfRequired()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                DisruptyPreferenceActivity.start(this)
                return true
            }
            R.id.action_log -> {
                DisruptionCheckActivity.start(this)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
