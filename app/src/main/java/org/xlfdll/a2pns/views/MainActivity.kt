package org.xlfdll.a2pns.views

import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airbnb.epoxy.EpoxyRecyclerView
import dagger.android.support.DaggerAppCompatActivity
import org.xlfdll.a2pns.App
import org.xlfdll.a2pns.NotificationListener
import org.xlfdll.a2pns.NotificationReceiver
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.base.ViewModelFactory
import org.xlfdll.a2pns.helpers.ViewHelper
import org.xlfdll.a2pns.viewmodels.NotificationListViewModel
import org.xlfdll.a2pns.views.controllers.NotificationListController
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var notificationListController: NotificationListController

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var notificationListViewModel: NotificationListViewModel
    private lateinit var notificationReceiver: NotificationReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.actionToolbar))

        // Has to set title here, as manifest's label attributes do not work consistently
        this.title = getString(R.string.app_title)

        val notificationRecyclerView =
            findViewById<EpoxyRecyclerView>(R.id.notificationRecyclerView)
        val notificationEmptyTextView = findViewById<TextView>(R.id.notificationEmptyTextView)

        // Basically, ViewModels should not be injected directly
        notificationListViewModel =
            ViewModelProvider(this, viewModelFactory)[NotificationListViewModel::class.java]
        notificationReceiver = NotificationReceiver(notificationListViewModel)

        if (!sharedPreferences.getBoolean(getString(R.string.pref_key_is_first_run_done), false)) {
            startActivity(Intent(this, StartupActivity::class.java))
        }

        notificationRecyclerView.setController(notificationListController)

        // BUG: For some reason ViewModels always got recreated here (thus yields empty notification list)
        // Using Room to persist all notifications (until app restarts)
        notificationListViewModel.notificationLiveView.observe(this, Observer { notifications ->
            notificationListController.setData(notifications)

            notificationEmptyTextView.visibility =
                if (notifications.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        })

        registerReceiver(notificationReceiver, IntentFilter(App.NOTIFICATION_SERVICE_ACTION))

        // Notification listener service will not work if app crashed
        // This forces to reconnect the service back to the system
        NotificationListener.reconnect(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()

        // registerReceiver() and unregisterReceiver() must match the lifecycle
        // e.g. registerReceiver() in onCreate() and unregisterReceiver() in onDestroy()
        unregisterReceiver(notificationReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    fun notificationAccessControlAction(view: View) {
        ViewHelper.openNotificationListenerSettings(this)
    }

    fun clearHistoryAction(view: View) {
        notificationListViewModel.clearNotifications()
    }
}