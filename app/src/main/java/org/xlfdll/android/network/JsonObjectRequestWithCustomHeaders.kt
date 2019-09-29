// (C) 2019 Xlfdll Workstation
// Used with permission

package org.xlfdll.android.network

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class JsonObjectRequestWithCustomHeaders(
    method: Int,
    url: String,
    private val customHeaders: MutableMap<String, String>?,
    jsonRequest: JSONObject,
    listener: Response.Listener<JSONObject>,
    errorListener: Response.ErrorListener
) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {
    init {
        if (customHeaders != null) {
            val originalHeaders = super.getHeaders()

            for (headerEntry in originalHeaders) {
                if (!customHeaders.containsKey(headerEntry.key)) {
                    customHeaders[headerEntry.key] = headerEntry.value
                }
            }
        }
    }

    override fun getHeaders(): MutableMap<String, String> {
        return customHeaders ?: super.getHeaders()
    }
}