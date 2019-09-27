package org.xlfdll.a2pns.helpers

import android.content.Context
import android.util.Log
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.models.NotificationItem

internal object DataHelper {
    fun logNotificationItem(context: Context, item: NotificationItem) {
        Log.i(
            context.getString(R.string.app_name),
            "Message from ${item.source} (${item.packageName})"
        )
    }
}