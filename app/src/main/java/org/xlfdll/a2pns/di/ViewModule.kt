package org.xlfdll.a2pns.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector
import org.xlfdll.a2pns.App
import org.xlfdll.a2pns.NotificationListener
import javax.inject.Singleton

@Module(
    includes = [
        AndroidInjectionModule::class,
        ActivityModule::class,
        FragmentModule::class,
        EpoxyModule::class,
        ViewDataModule::class
    ]
)
abstract class ViewModule {
    // Expect: Context
    // Given: App
    @Binds
    @Singleton
    abstract fun context(app: App): Context

    @ContributesAndroidInjector
    abstract fun notificationListener(): NotificationListener
}