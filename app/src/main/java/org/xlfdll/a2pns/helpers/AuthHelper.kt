package org.xlfdll.a2pns.helpers

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.models.ExternalData

internal object AuthHelper {
    fun updateAPNSAuthToken(context: Context) {
        val url = AppHelper.Settings.getString(
            context.getString(R.string.pref_key_custom_auth_token_url),
            ExternalData.APNSAuthTokenURL
        )
        val secret = AppHelper.Settings.getString(
            context.getString(R.string.pref_key_custom_auth_token_secret),
            ExternalData.DecryptionSecret
        )

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                if (!handleAuthResponse(context, response, secret)) {
                    ViewHelper.showAPSTokenUpdatedToast(context)
                } else {
                    ViewHelper.showAPSTokenLatestToast(context)
                }
            },
            Response.ErrorListener { _ ->
                ViewHelper.showAPSTokenErrorAlert(context)
            })

        AppHelper.HttpRequestQueue.add(request)
    }

    private fun handleAuthResponse(
        context: Context,
        response: JSONObject,
        secret: String?
    ): Boolean {
        val isAuthTokenLatest = response.getString("time") == AppHelper.Settings.getString(
            context.getString(R.string.pref_key_auth_token_update_date),
            null
        )

        if (!isAuthTokenLatest) {
            val certData = response.getString("cert").split(":")
            val token =
                CryptoHelper.decrypt(
                    certData[0],
                    certData[1],
                    certData[2],
                    secret!!
                )

            saveAPSToken(context, token, response.getString("time"))
        }

        return isAuthTokenLatest
    }

    private fun saveAPSToken(
        context: Context,
        token: String?,
        time: String
    ) {
        val prefEditor = AppHelper.Settings.edit()

        prefEditor.putString(context.getString(R.string.pref_key_auth_token), token)
            .putString(
                context.getString(R.string.pref_key_auth_token_update_date),
                time
            )
            .commit()
    }
}