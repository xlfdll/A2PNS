package org.xlfdll.a2pns.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import dagger.android.support.DaggerFragment
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.views.QRCodeActivity

class StartupPairDevicesFragment : DaggerFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_startup_pair_devices, container, false)
        val button = view.findViewById<Button>(R.id.pairDevicesButton)

        button.setOnClickListener {
            showDeviceTokenPrompt()
        }

        return view
    }

    private fun showDeviceTokenPrompt() {
        startActivity(Intent(context, QRCodeActivity::class.java))
    }
}