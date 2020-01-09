package org.xlfdll.a2pns.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.models.NotificationItem
import org.xlfdll.a2pns.services.db.NotificationCacheDao
import javax.inject.Inject

// For Dagger dependency injection, constructor must use @Inject even if no parameters are used
class NotificationListViewModel @Inject constructor(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val notificationCacheDao: NotificationCacheDao
) : ViewModel() {
    private var maxNotificationCacheSize = 50
    private val notificationList: MutableList<NotificationItem> = mutableListOf()

    private val mutableNotificationLiveView: MutableLiveData<List<NotificationItem>> by lazy {
        MutableLiveData<List<NotificationItem>>()
    }

    val notificationLiveView: LiveData<List<NotificationItem>> = mutableNotificationLiveView

    init {
        viewModelScope.launch {
            // Preference UI can only save values to strings
            maxNotificationCacheSize = sharedPreferences.getString(
                context.getString(R.string.pref_key_cached_notification_count),
                "50"
            )?.toInt() ?: 50

            val cachedNotifications = notificationCacheDao.getAllNotifications()

            notificationList.addAll(cachedNotifications.reversed())

            mutableNotificationLiveView.value = notificationList
        }
    }

    fun addNotification(item: NotificationItem) {
        viewModelScope.launch {
            notificationList.add(0, item)
            notificationCacheDao.addNotifications(item)

            if (notificationList.size > maxNotificationCacheSize) {
                val oldestNotificationItem = notificationList[notificationList.size - 1]

                notificationList.removeAt(notificationList.size - 1)
                notificationCacheDao.removeNotification(oldestNotificationItem)
            }

            // LiveData's value must be set in main thread
            mutableNotificationLiveView.value = notificationList
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            notificationList.clear()
            notificationCacheDao.clearNotifications()

            mutableNotificationLiveView.value = notificationList
        }
    }
}