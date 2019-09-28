package org.xlfdll.a2pns.helpers

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.models.ExternalData
import org.xlfdll.a2pns.models.NotificationItem

internal object ViewHelper {
    val NotificationItemList = ArrayList<NotificationItem>()

    fun showAPSTokenUpdatedToast(context: Context) {
        Toast.makeText(
            context,
            context.getString(R.string.toast_token_updated_message),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    fun showAPSTokenLatestToast(context: Context) {
        Toast.makeText(
            context,
            context.getString(R.string.toast_token_no_need_to_update_message),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    fun showAPSTokenErrorAlert(context: Context) {
        AlertDialog.Builder(context)
            .setMessage(R.string.alert_token_download_error_message)
            .setPositiveButton("OK", null)
            .show()
    }
}