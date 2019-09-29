package org.xlfdll.a2pns

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import org.json.JSONException
import org.json.JSONObject
import org.xlfdll.a2pns.helpers.AppHelper

class QRCodeActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        requestCameraPermissions()

        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = createCodeScanner(scannerView)

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun requestCameraPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 0)
        }
    }

    private fun createCodeScanner(scannerView: CodeScannerView): CodeScanner {
        val codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                val token = getDeviceToken(it.text)

                if (token != null) {
                    saveDeviceToken(token)
                    showPairSuccessToast()

                    if (AppHelper.isNotificationListenerEnabled(this)) {
                        createPairSuccessNotification()
                    }

                    finish()
                } else {
                    codeScanner.startPreview()
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback.SUPPRESS

        return codeScanner
    }

    private fun getDeviceToken(json: String): String? {
        try {
            val jsonObject = JSONObject(json)

            when (jsonObject.getString("id") == getString(R.string.id_qr_code_ios)) {
                true -> return jsonObject.getString("token")
            }
        } catch (_: JSONException) {
            return null
        }

        return null
    }

    private fun saveDeviceToken(token: String?) {
        val prefEditor = AppHelper.settings.edit()

        prefEditor.putString(getString(R.string.pref_key_device_token), token)
            .commit()
    }

    private fun showPairSuccessToast() {
        Toast.makeText(
            this,
            getString(R.string.toast_device_token_pair_success_message),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showCameraErrorToast(it: Exception) {
        Toast.makeText(
            this, getString(R.string.toast_device_token_camera_init_error) + it.message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun createPairSuccessNotification() {
        AppHelper.createAPNSNotificationChannel(this)

        val notification = NotificationCompat.Builder(this, AppHelper.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.app_title))
            .setContentText(getString(R.string.toast_device_token_pair_success_message))
            .build()

        val notifier = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notifier.notify(AppHelper.NOTIFICATION_PAIR_SUCCESS_ID, notification)
    }
}
