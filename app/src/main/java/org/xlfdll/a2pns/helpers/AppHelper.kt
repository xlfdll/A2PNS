package org.xlfdll.a2pns.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import androidx.core.app.NotificationManagerCompat
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
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
                        hashSetOf("org.xlfdll.a2pns")
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