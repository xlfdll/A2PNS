package org.xlfdll.a2pns.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_startup_finish.*
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.StartupActivity
import org.xlfdll.a2pns.helpers.AppHelper

class StartupFinishFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_startup_finish, container, false)
        val button = view.findViewById<Button>(R.id.enableServiceButton)

        button.setOnClickListener {
            enableListenerService()
        }

        return view
    }

    private fun enableListenerService() {
        val activity = context as StartupActivity

        val isNotificationListenerEnabled = AppHelper.isNotificationListenerEnabled(context!!)
        val isDevicePaired = AppHelper.isDevicePaired(context!!)

        if (isNotificationListenerEnabled && isDevicePaired) {
            AppHelper.Settings.edit()
                .putBoolean(getString(R.string.pref_key_enable_service), true)
                .commit()

            enableServiceButton.isEnabled = false
        } else if (!isNotificationListenerEnabled) {
            activity.showListenerPermissionNotGrantedToast()
        } else if (!isDevicePaired) {
            activity.showDeviceNotPairedToast()
        }
    }
}