package org.xlfdll.a2pns.services.db

import androidx.room.*
import org.xlfdll.a2pns.models.NotificationItem

@Dao
interface NotificationCacheDao {
    @Query("SELECT * FROM Notifications")
    suspend fun getAllNotifications(): List<NotificationItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNotifications(vararg notifications: NotificationItem)

    @Delete
    suspend fun removeNotification(notificationItem: NotificationItem)

    @Query("DELETE FROM Notifications")
    suspend fun clearNotifications()
}