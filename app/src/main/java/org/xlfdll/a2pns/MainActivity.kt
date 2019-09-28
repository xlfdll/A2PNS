package org.xlfdll.a2pns

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
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

        val filter = IntentFilter("org.xlfdll.a2pns.NOTIFICATION_SERVICE")

        registerReceiver(receiver, filter)

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
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)

        super.onDestroy()
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
            showNotificationIcon()
        } else {
            hideNotificationIcon()
        }
    }

    private fun showNotificationIcon() {
        createAPNSNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this, AppHelper.NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.app_title))
            .setContentText(getString(R.string.notification_running_text))
            .setContentIntent(pendingIntent)
            .build()

        notification.flags =
            notification.flags or Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT

        val notifier = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notifier.notify(AppHelper.NOTIFICATION_ID, notification)
    }

    private fun hideNotificationIcon() {
        val notifier =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notifier.cancel(AppHelper.NOTIFICATION_ID)
    }

    private fun createAPNSNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                AppHelper.NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.description = getString(R.string.notification_channel_description)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
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