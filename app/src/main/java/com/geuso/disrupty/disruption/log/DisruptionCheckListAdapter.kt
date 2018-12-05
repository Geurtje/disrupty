package com.geuso.disrupty.disruption.log

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.geuso.disrupty.R
import com.geuso.disrupty.R.id.*
import com.geuso.disrupty.db.InstantConverter
import com.geuso.disrupty.disruption.model.DisruptionCheck

class DisruptionCheckListAdapter (
        context: Context,
        items: List<DisruptionCheck>
) : ArrayAdapter<DisruptionCheck>(context, rowLayoutId, disruption_check_row_id, items) {
    companion object {
        private const val rowLayoutId = R.layout.disruption_check_list_row

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val disruptionCheck = getItem(position)
        val statusText = if (disruptionCheck.isDisrupted) R.string.disruption_check_status_failure
            else R.string.disruption_check_status_success
        val checkResult = if (disruptionCheck.success) R.string.disruption_check_success
            else R.string.disruption_check_failure


        val view = convertView ?:  LayoutInflater.from(context).inflate(rowLayoutId, parent, false)
        assignTextToView(view, disruption_check_row_id, disruptionCheck.id.toString())
        assignTextToView(view, disruption_check_subscription_id, disruptionCheck.subscriptionId.toString())

        assignTextToView(view, disruption_check_status, context.resources.getString(statusText))
        assignTextToView(view, disruption_check_result, context.resources.getString(checkResult))
        assignTextToView(view, disruption_check_timestamp, InstantConverter().format(disruptionCheck.timestamp))

        return view
    }

    private fun assignTextToView(rowView: View, id: Int, text: String) {
        rowView.findViewById<TextView>(id).text = text
    }

}


