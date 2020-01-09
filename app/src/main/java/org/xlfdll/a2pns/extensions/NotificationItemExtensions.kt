package org.xlfdll.a2pns.extensions

import android.content.Context
import android.util.Log
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.models.NotificationItem

internal object NotificationItemExtensions {
    fun NotificationItem.log(context: Context) {
        Log.i(
            context.getString(R.string.app_name),
            "Message from ${this.source} (${this.packageName})"
        )
    }
}