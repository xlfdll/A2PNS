package org.xlfdll.a2pns.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.xlfdll.a2pns.ExternalData
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.models.NotificationItem
import org.xlfdll.a2pns.models.apple.APNSRequest
import org.xlfdll.a2pns.services.apple.APNSService
import org.xlfdll.a2pns.services.apple.AuthTokenService
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

    var hasIncorrectClock: Boolean = false

    fun checkAuthTokenExpiration(): Boolean {
        val nowDateTime = LocalDateTime.now(ZoneOffset.UTC).atZone(ZoneOffset.UTC).toEpochSecond()
        val tokenIssuedTime = sharedPreferences.getLong(
            context.getString(R.string.pref_key_auth_token_issued_time),
            -1
        )

        if (tokenIssuedTime != -1L) {
            val timeDifference = nowDateTime - tokenIssuedTime

            hasIncorrectClock = (timeDifference < 0)

            if (hasIncorrectClock) {
                throw IllegalStateException("Current date and time is incorrect (much slower than normal).")
            }

            return ((timeDifference / 60) >= 50)
        }

        return true
    }

    suspend fun updateAuthToken() {
        val baseURL = sharedPreferences.getString(
            context.getString(R.string.pref_key_custom_auth_token_url),
            ExternalData.AuthDataURL
        )
        val authToken = authTokenService.getAPNSAuthToken(baseURL!!)

        sharedPreferences.edit()
            .putString(context.getString(R.string.pref_key_auth_token_id), authToken.id)
            .putString(context.getString(R.string.pref_key_auth_token), "bearer ${authToken.jwt}")
            .putLong(context.getString(R.string.pref_key_auth_token_issued_time), authToken.iat)
            .apply()
    }

    suspend fun sendNotificationRequest(notificationItem: NotificationItem) {
        if (checkAuthTokenExpiration()) {
            updateAuthToken()
        }

        if (!checkAuthTokenExpiration()) {
            val deviceToken =
                sharedPreferences.getString(context.getString(R.string.pref_key_device_token), "")
            val authToken =
                sharedPreferences.getString(context.getString(R.string.pref_key_auth_token), "")
            val id =
                sharedPreferences.getString(context.getString(R.string.pref_key_auth_token_id), "")
            val request = APNSRequest(notificationItem)

            apnsService.sendNotificationRequest(deviceToken!!, authToken!!, id!!, request)
        }
    }
}