package org.xlfdll.a2pns.views

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import dagger.android.support.DaggerAppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import org.xlfdll.a2pns.App
import org.xlfdll.a2pns.NotificationListener
import org.xlfdll.a2pns.R
import javax.inject.Inject

class QRCodeActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

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

    // Helper Methods

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
                val token = extractDeviceToken(JSONObject(it.text))

                if (token != null) {
                    saveDeviceToken(token)

                    showPairSuccessToast()

                    if (NotificationListener.isEnabled(this)) {
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

    private fun requestCameraPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 0)
        }
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
        App.createNotificationChannel(this)

        val notification = NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.app_title))
            .setContentText(getString(R.string.toast_device_token_pair_success_message))
            .build()

        val notifier = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notifier.notify(App.NOTIFICATION_PAIR_SUCCESS_ID, notification)
    }

    private fun extractDeviceToken(jsonObject: JSONObject): String? {
        return try {
            when (jsonObject.getString("id") == getString(R.string.id_qr_code_ios)) {
                true -> jsonObject.getString("token")
                else -> null
            }
        } catch (_: JSONException) {
            null
        }
    }

    private fun saveDeviceToken(token: String?) {
        sharedPreferences.edit()
            .putString(getString(R.string.pref_key_device_token), token)
            .apply()
    }
}
