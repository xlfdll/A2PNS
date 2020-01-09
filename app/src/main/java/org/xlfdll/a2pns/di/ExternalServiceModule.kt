package org.xlfdll.a2pns.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.xlfdll.a2pns.ExternalData
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.services.apple.APNSService
import org.xlfdll.a2pns.services.apple.AuthTokenService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ExternalServiceModule {
    @Provides
    @Singleton
    fun providesSharedPreferences(context: Context): SharedPreferences {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        if (sharedPreferences.getStringSet(
                context.getString(R.string.pref_key_selected_apps),
                null
            ) == null
        ) {
            sharedPreferences.edit()
                .putStringSet(
                    context.getString(R.string.pref_key_selected_apps),
                    hashSetOf()
                )
                .apply()
        }

        return sharedPreferences
    }

    @Provides
    @Singleton
    fun providesHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)

        if (ExternalData.DebugMode) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()

            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)
        }

        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun providesAuthTokenService(client: OkHttpClient): AuthTokenService {
        // Base URL must be set with '/' at the end. Use @Url to override
        return Retrofit.Builder()
            .client(client)
            .baseUrl(ExternalData.AuthDataURL + "/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthTokenService::class.java)
    }

    @Provides
    @Singleton
    fun providesApplePushNotificationService(client: OkHttpClient): APNSService {
        // Development server: api.sandbox.push.apple.com:443
        // Production server: api.push.apple.com:443
        val serverURL = if (ExternalData.DebugMode) {
            "https://api.sandbox.push.apple.com"
        } else {
            "https://api.push.apple.com"
        }

        return Retrofit.Builder()
            .client(client)
            .baseUrl(serverURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APNSService::class.java)
    }
}