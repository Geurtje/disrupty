package com.geuso.disrupty.model.ns

import android.preference.PreferenceManager
import com.geuso.disrupty.App
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams

object NsRestClient {

    private val TAG = NsRestClient::class.qualifiedName
    private const val BASE_URL = "http://webservices.ns.nl"
    const val PATH_STATIONS_LIST = "/ns-api-stations-v2"

    private val client = AsyncHttpClient()

    init {
        val preferences = PreferenceManager.getDefaultSharedPreferences(App.context)

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

}