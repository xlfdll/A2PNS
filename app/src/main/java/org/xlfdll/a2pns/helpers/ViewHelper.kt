package org.xlfdll.a2pns.helpers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import org.xlfdll.a2pns.App
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.views.MainActivity

internal object ViewHelper {
    fun openNotificationListenerSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        } else {
            context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
    }

    fun showAuthTokenUpdatedToast(context: Context) {
        Toast.makeText(
            context,
            context.getString(R.string.toast_token_updated_message),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    fun showAuthTokenLatestToast(context: Context) {
        Toast.makeText(
            context,
            context.getString(R.string.toast_token_no_need_to_update_message),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    fun showAuthTokenErrorAlert(context: Context) {
        AlertDialog.Builder(context)
            .setMessage(R.string.alert_token_download_error_message)
            .setPositiveButton("OK", null)
            .show()
    }

    fun showIncorrectClockErrorAlert(context: Context) {
        AlertDialog.Builder(context)
            .setMessage(R.string.alert_token_incorrect_clock_error_message)
            .setPositiveButton("OK", null)
            .show()
    }

    fun showIncorrectClockErrorNotification(context: Context) {
        App.createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, App.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.app_title))
            .setContentText(context.getString(R.string.alert_token_incorrect_clock_error_message))
            .build()

        val notifier = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notifier.notify(App.NOTIFICATION_INCORRECT_CLOCK_ID, notification)
    }

    fun showStatusIconNotification(context: Context): Notification {
        App.createNotificationChannel(context)

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(context, App.NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.app_title))
            .setContentText(context.getString(R.string.notification_running_text))
            .setContentIntent(pendingIntent)
            .build()

        notification.flags =
            notification.flags or Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT

        return notification
    }
}