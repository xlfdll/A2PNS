package org.xlfdll.a2pns

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.xlfdll.a2pns.models.NotificationItem
import org.xlfdll.a2pns.viewmodels.NotificationListViewModel

class NotificationReceiver(private val notificationListViewModel: NotificationListViewModel) :
    BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val item = intent?.getParcelableExtra<NotificationItem>("notification_item")

        if (item != null) {
            notificationListViewModel.addNotification(item)
        }
    }
}