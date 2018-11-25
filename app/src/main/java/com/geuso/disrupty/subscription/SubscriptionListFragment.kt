package com.geuso.disrupty.subscription

import android.app.ListFragment
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.geuso.disrupty.R
import com.geuso.disrupty.db.AppDatabase

class SubscriptionListFragment : ListFragment() {

    companion object {
        val TAG = SubscriptionListFragment::class.qualifiedName
    }

    override fun onResume() {
        super.onResume()

        val allSubscriptions = AppDatabase.getInstance(context).subscriptionDao().getAllSubscriptions()
        val subscriptionListAdapter = SubscriptionListAdapter(context, allSubscriptions.toList())
        super.setListAdapter(subscriptionListAdapter)
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        if (v != null) {
            val subId = v.findViewById<TextView>(R.id.sub_row_id).text.toString().toLong()
            Log.i(TAG, "Clicked on subscription at position $position, id: $subId")
            EditSubscriptionActivity.start(context, subId)
        }

    }

}