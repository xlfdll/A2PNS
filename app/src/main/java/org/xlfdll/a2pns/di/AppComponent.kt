package org.xlfdll.a2pns.di

import dagger.Component
import dagger.android.AndroidInjector
import org.xlfdll.a2pns.App
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ExternalServiceModule::class,
        ViewModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {
    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<App>
}