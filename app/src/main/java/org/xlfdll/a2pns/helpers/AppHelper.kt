package org.xlfdll.a2pns.helpers

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
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

    var isLaunched = false

    fun init(context: Context) {
        if (ExternalData.DebugMode) {
            APNSServerURL = "https://api.sandbox.push.apple.com"
        } else {
            APNSServerURL = "https://api.push.apple.com"
        }

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

        if (!(AppHelper::HttpRequestQueue.isInitialized)) {
            HttpRequestQueue = Volley.newRequestQueue(context, OkHttpStack())
        }
    }
}