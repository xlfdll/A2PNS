package org.xlfdll.a2pns.views.controllers

import com.airbnb.epoxy.TypedEpoxyController
import org.xlfdll.a2pns.models.NotificationItem
import org.xlfdll.a2pns.viewNotificationItem

class NotificationListController : TypedEpoxyController<List<NotificationItem>>() {
    override fun buildModels(data: List<NotificationItem>?) {
        // Wrap model object
        data?.forEach { notificationItem ->
            // Must be same as XML layout file name
            // e.g. viewNotificationItem <-> view_notification_item.xml
            viewNotificationItem {
                // Must have unique ID for each model instance
                id(notificationItem.hashCode())
                item(notificationItem)
            }
        }
    }
}