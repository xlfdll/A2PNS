package org.xlfdll.a2pns.services.db

import androidx.room.Database
import androidx.room.RoomDatabase
import org.xlfdll.a2pns.models.NotificationItem

@Database(version = 1, entities = [NotificationItem::class])
abstract class NotificationCacheDatabase : RoomDatabase() {
    abstract fun notificationCacheDao(): NotificationCacheDao
}