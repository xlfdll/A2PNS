package org.xlfdll.a2pns

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import org.xlfdll.a2pns.models.NotificationItem

class MainActivity : AppCompatActivity() {
    private val receiver = NotificationServiceReceiver()

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

        val filter = IntentFilter("org.xlfdll.a2pns.NOTIFICATION_SERVICE")

        registerReceiver(receiver, filter)

        enableSwitch.isChecked =
            AppHelper.Settings.getBoolean(getString(R.string.pref_key_enable_service), false)
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(receiver)
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
        ViewHelper.NotificationItemList.clear()

        notificationRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun initNotificationList() {
        notificationRecyclerView.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter =
                NotificationListAdapter(ViewHelper.NotificationItemList)
        }
    }

    private fun handleEnableSwitchStateChange(isChecked: Boolean) {
        AppHelper.Settings.edit()
            .putBoolean(getString(R.string.pref_key_enable_service), isChecked)
            .commit()

        if (isChecked) {
            ViewHelper.showNotificationIcon(this)
        } else {
            ViewHelper.hideNotificationIcon(this)
        }
    }

    inner class NotificationServiceReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val item = intent?.getParcelableExtra<NotificationItem>("notification_item")

            if (item != null) {
                ViewHelper.NotificationItemList.add(0, item)

                notificationRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }
}