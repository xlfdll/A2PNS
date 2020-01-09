package org.xlfdll.a2pns.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.xlfdll.a2pns.views.*

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract fun startupActivity(): StartupActivity

    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun settingsActivity(): SettingsActivity

    @ContributesAndroidInjector
    abstract fun qrCodeActivity(): QRCodeActivity

    @ContributesAndroidInjector
    abstract fun appListActivity(): AppListActivity
}