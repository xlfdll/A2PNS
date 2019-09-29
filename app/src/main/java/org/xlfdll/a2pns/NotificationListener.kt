package org.xlfdll.a2pns

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import org.xlfdll.a2pns.helpers.AppHelper
import org.xlfdll.a2pns.helpers.CryptoHelper
import org.xlfdll.a2pns.helpers.DataHelper
import org.xlfdll.a2pns.helpers.ViewHelper
import org.xlfdll.a2pns.models.ExternalData
import org.xlfdll.a2pns.models.NotificationItem
import org.xlfdll.android.network.JsonObjectRequestWithCustomHeaders

class NotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        if (sbn?.id != AppHelper.NOTIFICATION_SERVICE_RUNNING_ID && AppHelper.Settings.getBoolean(
                getString(R.string.pref_key_enable_service),
                false
            )
        ) {
            val item = generateNotificationItem(sbn)

            if (AppHelper.Settings.getStringSet(
                    getString(R.string.pref_key_selected_apps),
                    null
                )?.contains(item.packageName) == true
            ) {
                if (!ExternalData.MockMode) {
                    sendNotificationItem(item)
                }

                DataHelper.logNotificationItem(this, item)
                ViewHelper.NotificationItemList.add(0, item)
            }
        }
    }

    private fun generateNotificationItem(sbn: StatusBarNotification?): NotificationItem {
        var source = sbn?.packageName

        if (source != null) {
            source = packageManager.getPackageInfo(source, 0).applicationInfo.loadLabel(
                packageManager
            ).toString()
        }

        // SpannableString cannot be casted to String directly. Use toString() to convert
        return NotificationItem(
            sbn?.notification?.extras?.get("android.title")?.toString() ?: "",
            sbn?.notification?.extras?.get("android.text")?.toString() ?: "",
            source ?: "<Unknown>",
            sbn?.packageName ?: ""
        )
    }

    private fun sendNotificationItem(item: NotificationItem) {
        val authToken =
            AppHelper.Settings.getString(getString(R.string.pref_key_auth_token), null)

        if (authToken != null) {
            val deviceToken = AppHelper.Settings.getString(
                getString(R.string.pref_key_device_token),
                ""
            )
            val request = generateAppleJSONObjectRequest(
                item, authToken, deviceToken!!
            )

            if (request != null) {
                AppHelper.HttpRequestQueue.add(request)
            }
        }
    }

    private fun generateAppleJSONObject(item: NotificationItem): JSONObject {
        val rootJsonObject = JSONObject()

        rootJsonObject.put("aps", JSONObject())

        val apnsJsonObject = rootJsonObject.getJSONObject("aps")

        apnsJsonObject.put("alert", JSONObject())

        val alertJsonObject = apnsJsonObject.getJSONObject("alert")

        alertJsonObject.put("title", item.title)
        alertJsonObject.put("subtitle", item.source)
        alertJsonObject.put("body", item.text)

        apnsJsonObject.put("content-available", 1)
        apnsJsonObject.put("mutable-content", 1)
        apnsJsonObject.put("sound", "default")

        rootJsonObject.put("package", item.packageName)

        return rootJsonObject
    }

    private fun generateAppleJSONObjectRequest(
        item: NotificationItem,
        authToken: String,
        deviceToken: String
    ): JsonObjectRequest? {
        val authTokenPackage = JSONObject(authToken)
        val jwt = CryptoHelper.getAPNSBearerToken(authTokenPackage)
        val headers = HashMap<String, String>()

        headers["Authorization"] = "bearer $jwt"
        headers["apns-push-type"] = "alert"
        headers["apns-topic"] = authTokenPackage.getString("id")

        if (jwt != null) {
            val jsonObject = generateAppleJSONObject(item)
            return JsonObjectRequestWithCustomHeaders(Request.Method.POST,
                AppHelper.APNSServerURL + "/3/device/${deviceToken}",
                headers,
                jsonObject,
                Response.Listener { response ->
                },
                Response.ErrorListener { error ->
                })
        }

        return null
    }
}