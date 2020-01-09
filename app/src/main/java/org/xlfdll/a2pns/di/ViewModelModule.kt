package org.xlfdll.a2pns.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import org.xlfdll.a2pns.base.ViewModelFactory
import org.xlfdll.a2pns.base.ViewModelKey
import org.xlfdll.a2pns.viewmodels.AppListViewModel
import org.xlfdll.a2pns.viewmodels.NotificationListViewModel
import org.xlfdll.a2pns.viewmodels.ServiceViewModel

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ServiceViewModel::class)
    abstract fun aerviceViewModel(viewModel: ServiceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationListViewModel::class)
    abstract fun notificationListViewModel(viewModel: NotificationListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AppListViewModel::class)
    abstract fun appListViewModel(viewModel: AppListViewModel): ViewModel
}