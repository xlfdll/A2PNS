package org.xlfdll.a2pns.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import androidx.core.app.NotificationManagerCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.models.ExternalData
import org.xlfdll.android.network.OkHttpStack

internal object AppHelper {
    const val NOTIFICATION_CHANNEL_ID = "org.xlfdll.a2pns.notification"
    const val NOTIFICATION_ID = 1

    // Development server: api.sandbox.push.apple.com:443
    // Production server: api.push.apple.com:443
    lateinit var APNSServerURL: String
    lateinit var Settings: SharedPreferences
    lateinit var HttpRequestQueue: RequestQueue

    fun init(context: Context) {
        initAPNSServerURL()
        initAppSettings(context)
        initHttpStack(context)
    }

    private fun initAPNSServerURL() {
        if (ExternalData.DebugMode) {
            APNSServerURL = "https://api.sandbox.push.apple.com"
        } else {
            APNSServerURL = "https://api.push.apple.com"
        }
    }

    private fun initAppSettings(context: Context) {
        if (!(AppHelper::Settings.isInitialized)) {
            Settings = PreferenceManager.getDefaultSharedPreferences(context)

            if (Settings.getStringSet(
                    context.getString(R.string.pref_key_selected_apps),
                    null
                ) == null
            ) {
                Settings.edit()
                    .putStringSet(
                        context.getString(R.string.pref_key_selected_apps),
                        HashSet<String>()
                    )
                    .commit()
            }
        }
    }

    private fun initHttpStack(context: Context) {
        if (!(AppHelper::HttpRequestQueue.isInitialized)) {
            HttpRequestQueue = Volley.newRequestQueue(context, OkHttpStack())
        }
    }

    fun createAPNSNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.description =
                context.getString(R.string.notification_channel_description)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

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

    fun isNotificationListenerEnabled(context: Context): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(context)
            .contains(context.packageName)
    }

    fun isDevicePaired(context: Context): Boolean {
        return AppHelper.Settings.getString(
            context.getString(R.string.pref_key_device_token),
            null
        ) != null
    }
}