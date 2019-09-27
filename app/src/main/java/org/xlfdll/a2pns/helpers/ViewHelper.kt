package org.xlfdll.a2pns.helpers

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.models.ExternalData
import org.xlfdll.a2pns.models.NotificationItem

internal object ViewHelper {
    val NotificationItemList = ArrayList<NotificationItem>()

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
                handleAuthResponse(context, response, secret)
            },
            Response.ErrorListener { error ->
                showAPSTokenErrorAlert(context)
            })

        AppHelper.HttpRequestQueue.add(request)
    }

    private fun handleAuthResponse(
        context: Context,
        response: JSONObject,
        secret: String?
    ) {
        if (response.getString("time") != AppHelper.Settings.getString(
                context.getString(R.string.pref_key_auth_token_update_date),
                null
            )
        ) {
            val certData = response.getString("cert").split(":")
            val token =
                CryptoHelper.decrypt(
                    certData[0],
                    certData[1],
                    certData[2],
                    secret!!
                )

            saveAPSToken(context, token, response.getString("time"))
            showAPSTokenUpdatedToast(context)
        } else {
            showAPSTokenLatestToast(context)
        }
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

    private fun showAPSTokenUpdatedToast(context: Context) {
        Toast.makeText(
            context,
            context.getString(R.string.toast_token_updated_message),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    private fun showAPSTokenLatestToast(context: Context) {
        Toast.makeText(
            context,
            context.getString(R.string.toast_token_no_need_to_update_message),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    private fun showAPSTokenErrorAlert(context: Context) {
        AlertDialog.Builder(context)
            .setMessage(R.string.alert_token_download_error_message)
            .setPositiveButton("OK", null)
            .show()
    }
}