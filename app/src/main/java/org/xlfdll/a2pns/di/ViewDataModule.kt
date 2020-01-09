package org.xlfdll.a2pns.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import org.xlfdll.a2pns.services.db.NotificationCacheDao
import org.xlfdll.a2pns.services.db.NotificationCacheDatabase
import javax.inject.Singleton

@Module
class ViewDataModule {
    @Provides
    @Singleton
    fun providesNotificationCacheDatabase(context: Context): NotificationCacheDao {
        return Room.databaseBuilder(context, NotificationCacheDatabase::class.java, "Notifications")
            .build()
            .notificationCacheDao()
    }
}