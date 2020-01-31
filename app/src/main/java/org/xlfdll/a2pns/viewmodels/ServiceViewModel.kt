package org.xlfdll.a2pns.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.coroutineScope
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.xlfdll.a2pns.ExternalData
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.models.NotificationItem
import org.xlfdll.a2pns.models.apple.APNSRequest
import org.xlfdll.a2pns.services.apple.APNSService
import org.xlfdll.a2pns.services.apple.AuthTokenService
import org.xlfdll.a2pns.services.db.NotificationCacheDao
import javax.inject.Inject

class ServiceViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var context: Context
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var authTokenService: AuthTokenService
    @Inject
    lateinit var apnsService: APNSService
    @Inject
    lateinit var notificationCacheDao: NotificationCacheDao

    var hasIncorrectClock: Boolean = false

    fun checkAuthTokenExpiration(): Boolean {
        val nowTime = LocalDateTime.now(ZoneOffset.UTC).atZone(ZoneOffset.UTC).toEpochSecond()
        val tokenIssuedTime = sharedPreferences.getLong(
            context.getString(R.string.pref_key_auth_token_issued_time),
            -1
        )

        Log.i(
            context.getString(R.string.app_name),
            "[INFO] Now epoch: $nowTime / Issued epoch: $tokenIssuedTime"
        )

        if (tokenIssuedTime != -1L) {
            val timeDifference = nowTime - tokenIssuedTime

            hasIncorrectClock = (timeDifference < 0)

            if (hasIncorrectClock) {
                Log.i(
                    context.getString(R.string.app_name),
                    "[ERROR] Incorrect clock"
                )

                throw IllegalStateException("Current date and time is incorrect (much slower than normal).")
            }

            return ((timeDifference / 60) >= 50)
        }

        return true
    }

    suspend fun updateAuthToken() {
        coroutineScope {
            Log.i(
                context.getString(R.string.app_name),
                "[INFO] Auth token update started"
            )

            val baseURL = sharedPreferences.getString(
                context.getString(R.string.pref_key_custom_auth_token_url),
                ExternalData.AuthDataURL
            )
            val authToken = authTokenService.getAPNSAuthToken(baseURL!!)

            sharedPreferences.edit()
                .putString(context.getString(R.string.pref_key_auth_token_id), authToken.id)
                .putString(
                    context.getString(R.string.pref_key_auth_token),
                    "bearer ${authToken.jwt}"
                )
                .putLong(
                    context.getString(R.string.pref_key_auth_token_issued_time),
                    authToken.iat
                )
                .apply()

            Log.i(
                context.getString(R.string.app_name),
                "[INFO] Auth token update finished"
            )
        }
    }

    suspend fun checkDuplicatedNotificationItem(notificationItem: NotificationItem): Boolean {
        val lastNotificationItem = notificationCacheDao.getLastNotification()

        if (lastNotificationItem == notificationItem) {
            Log.i(
                context.getString(R.string.app_name),
                "[INFO] Notification same as the last"
            )
        }

        return lastNotificationItem == notificationItem
    }

    suspend fun sendNotificationRequest(notificationItem: NotificationItem) {
        coroutineScope {
            if (!checkDuplicatedNotificationItem(notificationItem)) {
                if (checkAuthTokenExpiration()) {
                    updateAuthToken()
                }

                if (!checkAuthTokenExpiration()) {
                    Log.i(
                        context.getString(R.string.app_name),
                        "[INFO] Started sending notification"
                    )

                    val deviceToken =
                        sharedPreferences.getString(
                            context.getString(R.string.pref_key_device_token),
                            ""
                        )
                    val authToken =
                        sharedPreferences.getString(
                            context.getString(R.string.pref_key_auth_token),
                            ""
                        )
                    val id =
                        sharedPreferences.getString(
                            context.getString(R.string.pref_key_auth_token_id),
                            ""
                        )
                    val request = APNSRequest(notificationItem)

                    apnsService.sendNotificationRequest(deviceToken!!, authToken!!, id!!, request)

                    Log.i(
                        context.getString(R.string.app_name),
                        "[INFO] Finished sending notification"
                    )
                }
            }
        }
    }
}