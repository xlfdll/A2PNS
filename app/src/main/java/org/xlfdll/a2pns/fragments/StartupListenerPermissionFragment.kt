package org.xlfdll.a2pns.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.helpers.AppHelper

class StartupListenerPermissionFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_startup_listener_permission, container, false)
        val button = view.findViewById<Button>(R.id.grantListenerPermissionButton)

        button.setOnClickListener {
            openNotificationListenerSettings()
        }

        return view
    }

    private fun openNotificationListenerSettings() {
        AppHelper.openNotificationListenerSettings(context!!)
    }
}