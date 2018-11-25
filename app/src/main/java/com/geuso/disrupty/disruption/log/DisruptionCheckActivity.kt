package com.geuso.disrupty.disruption.log

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.geuso.disrupty.R
import com.geuso.disrupty.disruption.DisruptionService
import kotlinx.android.synthetic.main.disruption_check_overview.*

class DisruptionCheckActivity : AppCompatActivity(), View.OnClickListener {

    companion object {

        private val TAG = DisruptionCheckActivity::class.qualifiedName
        private const val LAYOUT_ID: Int = R.layout.disruption_check_overview

        fun start(context: Context) {
            context.startActivity(Intent(context, DisruptionCheckActivity::class.java))
        }
    }

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
        button_trigger_disruption_check.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v){
            button_trigger_disruption_check -> triggerDisruptionCheck()
        }
    }

    private fun triggerDisruptionCheck() {
        DisruptionService(applicationContext).notifyDisruptedSubscriptions()
    }


}
