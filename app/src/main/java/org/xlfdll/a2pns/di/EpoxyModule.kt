package org.xlfdll.a2pns.di

import dagger.Module
import dagger.Provides
import org.xlfdll.a2pns.views.controllers.AppListController
import org.xlfdll.a2pns.views.controllers.NotificationListController
import javax.inject.Singleton

@Module
class EpoxyModule {
    @Provides
    @Singleton
    fun providesAppListController() = AppListController()

    @Provides
    @Singleton
    fun providesNotificationListController() = NotificationListController()
}