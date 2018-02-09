package com.geuso.disrupty.subscription

import android.app.ListFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.geuso.disrupty.db.AppDatabase

class SubscriptionListFragment : ListFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val allSubscriptions = AppDatabase.INSTANCE.subscriptionDao().loadAllSubscriptions()

        val subscriptionListAdapter = SubscriptionListAdapter(context, allSubscriptions.toList())

        super.setListAdapter(subscriptionListAdapter)


        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)


    }

}