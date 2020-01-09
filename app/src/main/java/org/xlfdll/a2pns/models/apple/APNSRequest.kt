package org.xlfdll.a2pns.models.apple

import com.google.gson.annotations.SerializedName
import org.xlfdll.a2pns.models.NotificationItem

class APNSRequest(notificationItem: NotificationItem) {
    @SerializedName("package")
    val packageName = notificationItem.packageName
    @SerializedName("aps")
    val aps = APNSRequestAPS(notificationItem)

    inner class APNSRequestAPS(notificationItem: NotificationItem) {
        @SerializedName("content-available")
        val content_available = 1
        @SerializedName("mutable-content")
        val mutable_content = 1
        val sound = "default"
        val alert = APNSRequestAlert(notificationItem)
    }

    inner class APNSRequestAlert(notificationItem: NotificationItem) {
        val title = notificationItem.title
        val subtitle = notificationItem.source
        val body = notificationItem.text
    }
}