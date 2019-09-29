package org.xlfdll.a2pns

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.xlfdll.a2pns.adapters.NotificationListAdapter
import org.xlfdll.a2pns.helpers.AppHelper
import org.xlfdll.a2pns.helpers.ViewHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.actionToolbar))

        // Has to set title here, as manifest's label attributes do not work consistently
        this.title = getString(R.string.app_title)

        // App settings
        AppHelper.init(applicationContext)

        initNotificationList()

        enableSwitch.setOnCheckedChangeListener { _, isChecked ->
            handleEnableSwitchStateChange(isChecked)
        }

        if (!AppHelper.Settings.getBoolean(
                getString(R.string.pref_ns_key_is_first_run_done),
                false
            )
        ) {
            startActivity(Intent(this, StartupActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        enableSwitch.isChecked =
            AppHelper.Settings.getBoolean(getString(R.string.pref_key_enable_service), false)

        notificationRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    fun clearHistoryAction(view: View) {
        ViewHelper.clearNotificationItems()
    }

    private fun initNotificationList() {
        notificationRecyclerView.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ViewHelper.notificationListAdapter
        }
    }

    private fun handleEnableSwitchStateChange(isChecked: Boolean) {
        AppHelper.Settings.edit()
            .putBoolean(getString(R.string.pref_key_enable_service), isChecked)
            .commit()

        if (isChecked) {
            startService(Intent(this, NotificationListener::class.java))
        } else {
            stopService(Intent(this, NotificationListener::class.java))
        }
    }
}