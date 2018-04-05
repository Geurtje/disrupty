package com.geuso.disrupty.subscription

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
) : Dialog(context), AdapterView.OnItemClickListener {

    companion object {
        private val TAG = StationSelectionDialog::class.qualifiedName
    }

    private lateinit var stationsListAdapter: StationsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.select_stations_dialog)

        stationsListAdapter = StationsListAdapter(this.context)
        stations_list.adapter = stationsListAdapter
        stations_list.onItemClickListener = this

        setFullScreenWindowProperties()

        search_field.addTextChangedListener(StationListFilterListener(this))
    }

    fun filterStations(filterTerm: String){
        stationsListAdapter.filterStationsByTerm(filterTerm)
    }

    private fun setFullScreenWindowProperties() {
        val windowParameters = WindowManager.LayoutParams()
        windowParameters.copyFrom(window.attributes)
        windowParameters.width = WindowManager.LayoutParams.MATCH_PARENT
        windowParameters.height = WindowManager.LayoutParams.MATCH_PARENT

        window.attributes = windowParameters
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        textField.setText(stationsListAdapter.getItem(position))
        dismiss()
    }

}

class StationsListAdapter(
        context: Context
) : ArrayAdapter<String>(context, R.layout.station_list_row) {

    companion object {
        private val TAG = StationsListAdapter::class.qualifiedName
    }

    private lateinit var stationsList: ArrayList<String>

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

    fun filterStationsByTerm(filterTerm: String){
        // Null check in case api call wasn't able to initialize the stationsList,
        // not sure if lateinit actually makes sense here
        if (stationsList != null) {
            val filteredStationsList = ArrayList<String>()

            for (stationName in stationsList) {
                if (stationName.contains(filterTerm, ignoreCase = false)){
                    filteredStationsList.add(stationName)
                }
            }

            clear()
            addAll(filteredStationsList)
        }
    }
}

class StationListFilterListener(
        private val selectionDialog: StationSelectionDialog
) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        selectionDialog.filterStations(s.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Intentionally empty, only apply a filter once the text has been updated
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Intentionally empty, only apply a filter once the text has been updated
    }

}