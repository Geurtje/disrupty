package com.geuso.disrupty.subscription

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.geuso.disrupty.R
import com.geuso.disrupty.model.ns.NsRestClient
import com.geuso.disrupty.model.ns.NsStationsXmlParser
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.select_stations_dialog.*

/**
 * Created by Tom on 3-4-2018.
 */
class StationSelectionDialog(
        context: Context,
        private val textField: AutoCompleteTextView
) : Dialog(context), View.OnClickListener, AdapterView.OnItemClickListener {

    companion object {
        private val TAG = StationSelectionDialog::class.qualifiedName
    }

    private var stationsListAdapter: StationsListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.select_stations_dialog)


        search_field.setOnClickListener(this)

        stationsListAdapter = StationsListAdapter(this.context)
        stations_list.adapter = stationsListAdapter
        stations_list.onItemClickListener = this

        setFullScreenWindowProperties()
    }

    private fun setFullScreenWindowProperties() {
        val windowParameters = WindowManager.LayoutParams()
        windowParameters.copyFrom(window.attributes)
        windowParameters.width = WindowManager.LayoutParams.MATCH_PARENT
        windowParameters.height = WindowManager.LayoutParams.MATCH_PARENT

        window.attributes = windowParameters
    }

    override fun onClick(v: View?) {
        if (v == null){
            return
        }

        when (v) {
            search_field -> textField.setText("foo!")
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        textField.setText(stationsListAdapter!!.getItem(position))
        dismiss()
    }

}

class StationsListAdapter(
        context: Context
) : ArrayAdapter<String>(context, R.layout.station_list_row) {

    companion object {
        private val TAG = StationsListAdapter::class.qualifiedName
    }

    private var stationsList: ArrayList<String>? = null

    init {
        initializeTrainStationsList()
    }

    private fun initializeTrainStationsList() {
        Log.i(TAG, "Retrieving stations from NS API")
        NsRestClient.get(NsRestClient.PATH_STATIONS_LIST, null, object: TextHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: String?) {
                val stationsObjectList = NsStationsXmlParser().parse(responseBody!!.byteInputStream())

                stationsList = ArrayList(stationsObjectList.size)
                for (station in stationsObjectList) {
                    stationsList!!.add(station.name)
                }

                addAll(stationsList)
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: String?, error: Throwable?) {
                Log.e(TAG, "Failure: $statusCode, body: $responseBody")
            }
        })
    }

}