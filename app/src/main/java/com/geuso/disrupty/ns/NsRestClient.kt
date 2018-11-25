package com.geuso.disrupty.ns

import android.content.Context
import android.preference.PreferenceManager
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams

class NsRestClient(context: Context) {

    companion object {
        private val TAG = NsRestClient::class.qualifiedName
        private const val BASE_URL = "http://webservices.ns.nl"
        const val PATH_STATIONS_LIST = "/ns-api-stations-v2"
        const val PATH_TRAVEL_OPTIONS = "/ns-api-treinplanner"
    }

    private val client = AsyncHttpClient()

    init {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        val username : String? = preferences.getString("api_ns_username", null)
        val password : String? = preferences.getString("api_ns_password", null)

        client.setBasicAuth(username, password)
    }


    fun get(url: String, params: RequestParams?, responseHandler: AsyncHttpResponseHandler) {
        client.get(absoluteUrl(url), params, responseHandler)
    }

    private fun absoluteUrl(relativeUrl: String): String{
        return BASE_URL + relativeUrl
    }

    fun paramsForTravelOptions(stationFrom : String, stationTo : String) : RequestParams {
        val paramsMap = HashMap<String, String>()

        paramsMap["fromStation"] = stationFrom
        paramsMap["toStation"] = stationTo
        paramsMap["previousAdvices"] = "0"
        paramsMap["nextAdvices"] = "2"

        return RequestParams(paramsMap)
    }

}