package org.xlfdll.a2pns.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.xlfdll.a2pns.views.fragments.StartupFinishFragment
import org.xlfdll.a2pns.views.fragments.StartupListenerPermissionFragment
import org.xlfdll.a2pns.views.fragments.StartupPairDevicesFragment
import org.xlfdll.a2pns.views.fragments.StartupSelectAppsFragment

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract fun startupListenerPermissionFragment(): StartupListenerPermissionFragment

    @ContributesAndroidInjector
    abstract fun startupPairDevicesFragment(): StartupPairDevicesFragment

    @ContributesAndroidInjector
    abstract fun startupSelectAppsFragment(): StartupSelectAppsFragment

    @ContributesAndroidInjector
    abstract fun startupFinishFragment(): StartupFinishFragment
}