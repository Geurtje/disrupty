package com.geuso.disrupty.disruption.log

import android.app.ListFragment
import com.geuso.disrupty.db.AppDatabase

class DisruptionCheckListFragment : ListFragment() {

    override fun onResume() {
        super.onResume()

        val disruptionCheckList = AppDatabase.INSTANCE.disruptionCheckDao().getLatestDisruptionChecks()
        super.setListAdapter(DisruptionCheckListAdapter(context, disruptionCheckList))
    }


}



