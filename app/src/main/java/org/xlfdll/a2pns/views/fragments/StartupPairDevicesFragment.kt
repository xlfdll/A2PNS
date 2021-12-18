package org.xlfdll.a2pns.views.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
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
        val companionAppTextView = view.findViewById<TextView>(R.id.companionAppTextView)

        button.setOnClickListener {
            showDeviceTokenPrompt()
        }

        companionAppTextView.text = HtmlCompat.fromHtml(
            getString(R.string.fragment_startup_device_token_companion_apps_text),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        companionAppTextView.movementMethod = LinkMovementMethod.getInstance()

        return view
    }

    private fun showDeviceTokenPrompt() {
        startActivity(Intent(context, QRCodeActivity::class.java))
    }
}