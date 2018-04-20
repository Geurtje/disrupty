package com.geuso.disrupty.subscription

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.geuso.disrupty.R
import com.geuso.disrupty.R.id.*
import com.geuso.disrupty.subscription.model.Subscription
import com.geuso.disrupty.subscription.model.TimeConverter

/**
 * Created by Tom on 4-2-2018.
 */
class SubscriptionListAdapter : ArrayAdapter<Subscription> {


    companion object {
        private const val rowLayoutId = R.layout.subscription_list_row

    }

    constructor(context: Context, items: List<Subscription>) : super(context, rowLayoutId, sub_row_id, items)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val subscription = getItem(position)

        val view = convertView ?:  LayoutInflater.from(context).inflate(rowLayoutId, parent, false)
        assignTextToView(view, sub_row_id, subscription.id.toString())
        assignTextToView(view, sub_row_station_from, subscription.stationFrom)
        assignTextToView(view, sub_row_station_to, subscription.stationTo)

        assignTextToView(view, sub_row_time_from, TimeConverter.INSTANCE.dateToTime(subscription.timeFrom))
        assignTextToView(view, sub_row_time_to, TimeConverter.INSTANCE.dateToTime(subscription.timeTo))

        return view
    }

    private fun assignTextToView(rowView: View, id: Int, text: String) {
        rowView.findViewById<TextView>(id).text = text
    }
}

