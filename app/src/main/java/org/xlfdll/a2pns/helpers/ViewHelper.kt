package org.xlfdll.a2pns.helpers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.xlfdll.a2pns.MainActivity
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.models.NotificationItem

internal object ViewHelper {
    val NotificationItemList = ArrayList<NotificationItem>()

    fun showAPSTokenUpdatedToast(context: Context) {
        Toast.makeText(
            context,
            context.getString(R.string.toast_token_updated_message),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    fun showAPSTokenLatestToast(context: Context) {
        Toast.makeText(
            context,
            context.getString(R.string.toast_token_no_need_to_update_message),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    fun showAPSTokenErrorAlert(context: Context) {
        AlertDialog.Builder(context)
            .setMessage(R.string.alert_token_download_error_message)
            .setPositiveButton("OK", null)
            .show()
    }

    fun showNotificationIcon(context: Context) {
        AppHelper.createAPNSNotificationChannel(context)

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(context, AppHelper.NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.app_title))
            .setContentText(context.getString(R.string.notification_running_text))
            .setContentIntent(pendingIntent)
            .build()

        notification.flags =
            notification.flags or Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT

        val notifier = NotificationManagerCompat.from(context)

        notifier.notify(AppHelper.NOTIFICATION_SERVICE_RUNNING_ID, notification)
    }

    fun hideNotificationIcon(context: Context) {
        val notifier =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notifier.cancel(AppHelper.NOTIFICATION_SERVICE_RUNNING_ID)
    }
}