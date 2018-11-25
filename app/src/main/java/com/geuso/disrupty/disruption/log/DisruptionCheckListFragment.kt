package com.geuso.disrupty.disruption.log

import android.app.ListFragment
import android.util.Log
import android.view.View
import android.widget.ListView
import com.geuso.disrupty.db.AppDatabase
import com.geuso.disrupty.disruption.model.DisruptionCheck

class DisruptionCheckListFragment : ListFragment() {

    companion object {
        private val TAG = DisruptionCheckListFragment::class.qualifiedName
    }

    override fun onResume() {
        super.onResume()

        val disruptionCheckList = AppDatabase.getInstance(context).disruptionCheckDao().getLatestDisruptionChecks()
        super.setListAdapter(DisruptionCheckListAdapter(context, disruptionCheckList))
    }


    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)

        if (v != null) {
            val disruptionCheck = listAdapter.getItem(position) as DisruptionCheck

            Log.i(TAG, "Clicked on disruption check at position $position, id: ${disruptionCheck.id}")
            DisruptionCheckDetailActivity.start(context, disruptionCheck)
        }
    }

}



