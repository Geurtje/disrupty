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
import android.widget.Toast
import com.geuso.disrupty.R
import com.geuso.disrupty.ns.NsPublicTravelRestClient
import com.geuso.disrupty.ns.station.NsStationsJsonParser
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.select_stations_dialog.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

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
        setSearchFieldFocus()
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

    private fun setSearchFieldFocus() {
        search_field.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
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

    // stationsList is mutable because it will be initialized later.
    // Using lateinit will crash the app in case the api call to resolve the stations failed.
    private var stationsList: ArrayList<String> = ArrayList(0)

    init {
        initializeTrainStationsList()
    }

    private fun initializeTrainStationsList() {
        Log.i(TAG, "Retrieving stations from NS API")
        NsPublicTravelRestClient(context).get(NsPublicTravelRestClient.PATH_STATIONS_LIST, null,  object: JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
                super.onSuccess(statusCode, headers, response)
                if (response != null) {
                    val stationsObjectList = NsStationsJsonParser().parseStations(response)

                    stationsList = ArrayList(stationsObjectList.size)
                    for (station in stationsObjectList) {
                        stationsList.add(station.name)
                    }

                    addAll(stationsList)
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: String?, throwable: Throwable?) {
                Log.e(TAG, "Failure: $statusCode, body: $responseBody", throwable)
                handleFailure(statusCode, throwable?.message)
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                Log.e(TAG, "Failure: $statusCode, body: $errorResponse", throwable)
                handleFailure(statusCode, errorResponse?.getString("message"))
            }
        })
    }

    private fun handleFailure(statusCode: Int, message: String?) {
        val message = when (statusCode) {
            401 -> context.resources.getString(R.string.ns_authentication_failure)
            else -> context.resources.getString(R.string.ns_stations_list_failure, message)
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun filterStationsByTerm(filterTerm: String){
        if (stationsList.isNotEmpty()) {
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
        selectionDialog.filterStations(s.toString().trim())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Intentionally empty, only apply a filter once the text has been updated
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Intentionally empty, only apply a filter once the text has been updated
    }

}