package org.xlfdll.a2pns

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import org.xlfdll.a2pns.di.DaggerAppComponent


class App : DaggerApplication() {
    companion object {
        const val NOTIFICATION_SERVICE_ACTION = "org.xlfdll.a2pns.NOTIFICATION_SERVICE"
        const val NOTIFICATION_CHANNEL_ID = "org.xlfdll.a2pns.notification"
        const val NOTIFICATION_SERVICE_RUNNING_ID = 1
        const val NOTIFICATION_PAIR_SUCCESS_ID = 2
        const val NOTIFICATION_INCORRECT_CLOCK_ID = 3

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )

                notificationChannel.description =
                    context.getString(R.string.notification_channel_description)

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }

    override fun applicationInjector(): AndroidInjector<App> {
        return DaggerAppComponent.factory().create(this)
    }
}