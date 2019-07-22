package com.geuso.disrupty.ns

import android.content.Context
import android.preference.PreferenceManager
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.message.BasicHeader

/**
 * Client for the public travel REST API.
 */
class NsPublicTravelRestClient(val context: Context) {

    companion object {
        private val TAG = NsPublicTravelRestClient::class.qualifiedName

        // https://gateway.apiportal.ns.nl/public-reisinformatie/api/v2/stations
        // GET /api/v3/trips
        private const val BASE_URL = "https://gateway.apiportal.ns.nl/public-reisinformatie/api/v2/"
        private const val API_KEY_HEADER = "Ocp-Apim-Subscription-Key"
        const val PATH_STATIONS_LIST = "stations"
    }

    private val client = AsyncHttpClient()
    private val apiKey : String

    init {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        // todo properly deal with null here
        apiKey = preferences.getString("api_ns_api_key", null)!!
    }


    fun get(relativeUrl: String, params: RequestParams?, responseHandler: AsyncHttpResponseHandler) {
        val url = BASE_URL + relativeUrl
        val headers = Array(1) { BasicHeader(API_KEY_HEADER, apiKey) }
        client.get(context, url, headers, params, responseHandler)
    }


}