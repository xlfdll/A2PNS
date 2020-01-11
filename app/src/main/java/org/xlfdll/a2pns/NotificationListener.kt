package org.xlfdll.a2pns

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import dagger.android.AndroidInjection
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.xlfdll.a2pns.App.Companion.NOTIFICATION_SERVICE_RUNNING_ID
import org.xlfdll.a2pns.extensions.NotificationItemExtensions.log
import org.xlfdll.a2pns.helpers.ViewHelper
import org.xlfdll.a2pns.models.NotificationItem
import org.xlfdll.a2pns.viewmodels.ServiceViewModel
import retrofit2.HttpException
import javax.inject.Inject

class NotificationListener : NotificationListenerService() {
    companion object {
        fun isEnabled(context: Context): Boolean {
            return NotificationManagerCompat.getEnabledListenerPackages(context)
                .contains(context.packageName)
        }

        fun reconnect(context: Context) {
            val packageManager: PackageManager = context.packageManager

            packageManager.setComponentEnabledSetting(
                ComponentName(context, NotificationListener::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
            )
            packageManager.setComponentEnabledSetting(
                ComponentName(context, NotificationListener::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
            )
        }
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var serviceViewModel: ServiceViewModel

    override fun onCreate() {
        AndroidInjection.inject(this)

        super.onCreate()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()

        startForeground(
            NOTIFICATION_SERVICE_RUNNING_ID,
            ViewHelper.showStatusIconNotification(this)
        )
    }

    override fun onListenerDisconnected() {
        stopForeground(true)

        super.onListenerDisconnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        if (sbn?.id != NOTIFICATION_SERVICE_RUNNING_ID) {
            val item = NotificationItem.create(this, sbn)

            if (sharedPreferences.getStringSet(
                    getString(R.string.pref_key_selected_apps),
                    setOf<String>()
                )!!.contains(item.packageName)
            ) {
                if (!ExternalData.MockMode) {
                    sendNotificationRequest(item, this)
                }

                item.log(this)

                broadcastNotificationItem(item)
            }
        }
    }

    private fun sendNotificationRequest(item: NotificationItem, context: Context) {
        MainScope().launch {
            if (!serviceViewModel.hasIncorrectClock) {
                try {
                    serviceViewModel.sendNotificationRequest(item)
                } catch (ex: IllegalStateException) {
                    ViewHelper.showIncorrectClockErrorNotification(context)
                } catch (ex: HttpException) {
                    Log.i(
                        context.getString(R.string.app_name),
                        "Error sending notification from ${item.source} (${item.packageName}) - (${ex.code()}) ${ex.message()}"
                    )
                } catch (ex: Throwable) {
                    Log.i(
                        context.getString(R.string.app_name),
                        "[ERROR] Unexpected error occurred: ${ex.message}"
                                + System.getProperty("line.separator")
                                + "Stacktrace:"
                                + System.getProperty("line.separator")
                    )

                    ex.printStackTrace()

                    if (ExternalData.DebugMode) {
                        throw ex
                    }
                }
            }
        }
    }

    private fun broadcastNotificationItem(item: NotificationItem) {
        val intent = Intent(App.NOTIFICATION_SERVICE_ACTION)

        intent.putExtra("notification_item", item)

        sendBroadcast(intent)
    }
}