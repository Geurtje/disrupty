package com.geuso.disrupty.subscription

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
) : Dialog(context), View.OnClickListener {

    companion object {
        private val TAG = StationSelectionDialog::class.qualifiedName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.select_stations_dialog)


        search_field.setOnClickListener(this)

        stations_list.adapter = StationsListAdapter(this.context)

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

}

class StationsListAdapter(context: Context): ArrayAdapter<String>(context, R.layout.station_list_row), AdapterView.OnItemClickListener {

    companion object {
        private val TAG = StationsListAdapter::class.qualifiedName
    }

    private var stationsList: ArrayList<String>? = null
    private val inflaterService: LayoutInflater

    init {
        initializeTrainStationsList()
        inflaterService  = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    }

    private fun initializeTrainStationsList() {
        Log.i(TAG, "Querying ns api!")
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}