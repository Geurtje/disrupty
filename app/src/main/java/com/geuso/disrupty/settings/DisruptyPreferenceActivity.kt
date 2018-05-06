package com.geuso.disrupty.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import com.geuso.disrupty.R

/**
 * Created by Tom on 30-1-2018.
 */
class DisruptyPreferenceActivity : PreferenceActivity() {

    companion object {
        fun start(context: Context){
            context.startActivity(Intent(context, DisruptyPreferenceActivity::class.java))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, DisruptyPreferenceFragment()).commit()

    }


    class DisruptyPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.settings)
        }
    }

}

