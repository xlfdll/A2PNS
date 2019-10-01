package org.xlfdll.a2pns.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.xlfdll.a2pns.MainActivity
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.models.ExternalData
import org.xlfdll.android.network.OkHttpStack

internal object AppHelper {
    const val NOTIFICATION_CHANNEL_ID = "org.xlfdll.a2pns.notification"
    const val NOTIFICATION_SERVICE_RUNNING_ID = 1
    const val NOTIFICATION_PAIR_SUCCESS_ID = 2

    // Development server: api.sandbox.push.apple.com:443
    // Production server: api.push.apple.com:443
    lateinit var apnsServerURL: String
    lateinit var settings: SharedPreferences
    lateinit var httpRequestQueue: RequestQueue
    lateinit var mainActivity: MainActivity

    var isInitialized = false

    fun init(context: Context) {
        initAPNSServerURL()
        initAppSettings(context)
        initHttpStack(context)

        mainActivity = context as MainActivity
        isInitialized = true
    }

    private fun initAPNSServerURL() {
        if (ExternalData.DebugMode) {
            apnsServerURL = "https://api.sandbox.push.apple.com"
        } else {
            apnsServerURL = "https://api.push.apple.com"
        }
    }

    private fun initAppSettings(context: Context) {
        if (!(AppHelper::settings.isInitialized)) {
            settings = PreferenceManager.getDefaultSharedPreferences(context)

            if (settings.getStringSet(
                    context.getString(R.string.pref_key_selected_apps),
                    null
                ) == null
            ) {
                settings.edit()
                    .putStringSet(
                        context.getString(R.string.pref_key_selected_apps),
                        hashSetOf("org.xlfdll.a2pns")
                    )
                    .commit()
            }
        }
    }

    private fun initHttpStack(context: Context) {
        if (!(AppHelper::httpRequestQueue.isInitialized)) {
            httpRequestQueue = Volley.newRequestQueue(context, OkHttpStack())
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
        return settings.getString(
            context.getString(R.string.pref_key_device_token),
            null
        ) != null
    }

    fun openNotificationListenerSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        } else {
            context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
    }

}