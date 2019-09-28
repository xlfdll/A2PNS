package org.xlfdll.a2pns.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import org.xlfdll.a2pns.AppListActivity
import org.xlfdll.a2pns.R

class StartupSelectAppsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_startup_select_apps, container, false)
        val button = view.findViewById<Button>(R.id.selectAppsButton)

        button.setOnClickListener {
            showWatchedAppList()
        }

        return view
    }

    private fun showWatchedAppList() {
        startActivity(Intent(requireContext(), AppListActivity::class.java))
    }
}