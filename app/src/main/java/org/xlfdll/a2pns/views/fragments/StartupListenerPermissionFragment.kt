package org.xlfdll.a2pns.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import dagger.android.support.DaggerFragment
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.helpers.ViewHelper

class StartupListenerPermissionFragment : DaggerFragment() {
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
        ViewHelper.openNotificationListenerSettings(context!!)
    }
}