package com.geuso.disrupty.subscription

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.geuso.disrupty.R
import com.geuso.disrupty.util.ButtonTimePicketDialog
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

    }

    override fun onClick(v: View?) {
        if (v == null){
            return
        }

        when (v) {
            button_time_from -> ButtonTimePicketDialog(this, true, button_time_from).show()
            button_time_to -> ButtonTimePicketDialog(this, true, button_time_to).show()
        }

    }

}