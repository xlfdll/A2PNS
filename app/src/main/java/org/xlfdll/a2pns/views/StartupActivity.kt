package org.xlfdll.a2pns.views

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.android.support.DaggerAppCompatActivity
import org.xlfdll.a2pns.NotificationListener
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.views.fragments.StartupFinishFragment
import org.xlfdll.a2pns.views.fragments.StartupListenerPermissionFragment
import org.xlfdll.a2pns.views.fragments.StartupPairDevicesFragment
import org.xlfdll.a2pns.views.fragments.StartupSelectAppsFragment
import javax.inject.Inject

class StartupActivity : DaggerAppCompatActivity() {
    companion object {
        const val STARTUP_PAGE_LISTENER_PERMISSION_INDEX = 0
        const val STARTUP_PAGE_PAIR_DEVICES_INDEX = 1
        const val STARTUP_PAGE_SELECT_APPS_INDEX = 2
        const val STARTUP_PAGE_FINISH_INDEX = 3
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var currentPageIndex = STARTUP_PAGE_LISTENER_PERMISSION_INDEX

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)

        val startupViewPager = findViewById<ViewPager2>(R.id.startupViewPager)

        startupViewPager.adapter = ViewPagerAdapter(this)
        startupViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                currentPageIndex = position

                updateNavigateButtonStates()
            }
        })
    }

    fun backAction(view: View) {
        val startupViewPager = findViewById<ViewPager2>(R.id.startupViewPager)

        if (currentPageIndex > STARTUP_PAGE_LISTENER_PERMISSION_INDEX) {
            startupViewPager.currentItem = currentPageIndex - 1
        }
    }

    fun nextAction(view: View) {
        val startupViewPager = findViewById<ViewPager2>(R.id.startupViewPager)

        if (currentPageIndex == STARTUP_PAGE_FINISH_INDEX) {
            finishAppFirstRun()
        } else if (currentPageIndex < STARTUP_PAGE_FINISH_INDEX) {
            startupViewPager.currentItem = currentPageIndex + 1
        }
    }

    private fun updateNavigateButtonStates() {
        val backButton = findViewById<Button>(R.id.backButton)
        val nextButton = findViewById<Button>(R.id.nextButton)

        backButton.isEnabled = (currentPageIndex > STARTUP_PAGE_LISTENER_PERMISSION_INDEX)
        nextButton.text =
            if (currentPageIndex < STARTUP_PAGE_FINISH_INDEX) {
                getString(R.string.button_startup_next)
            } else {
                getString(R.string.button_startup_finish)
            }
    }

    private fun finishAppFirstRun() {
        val isNotificationListenerEnabled = NotificationListener.isEnabled(this)
        val isDevicePaired =
            sharedPreferences.getString(
                getString(R.string.pref_key_device_token),
                null
            ) != null

        if (isNotificationListenerEnabled && isDevicePaired) {
            sharedPreferences.edit()
                .putBoolean(getString(R.string.pref_key_is_first_run_done), true)
                .apply()



            finish()
        } else if (!isNotificationListenerEnabled) {
            showListenerPermissionNotGrantedToast()
        } else if (!isDevicePaired) {
            showDeviceNotPairedToast()
        }
    }

    private fun showListenerPermissionNotGrantedToast() {
        Toast.makeText(
            this,
            this.getString(R.string.toast_startup_notification_listener_permission_not_granted),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    private fun showDeviceNotPairedToast() {
        Toast.makeText(
            this,
            this.getString(R.string.toast_startup_device_not_paired),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 4
        }

        override fun createFragment(position: Int): Fragment {
            when (position) {
                STARTUP_PAGE_LISTENER_PERMISSION_INDEX -> return StartupListenerPermissionFragment()
                STARTUP_PAGE_PAIR_DEVICES_INDEX -> return StartupPairDevicesFragment()
                STARTUP_PAGE_SELECT_APPS_INDEX -> return StartupSelectAppsFragment()
                STARTUP_PAGE_FINISH_INDEX -> return StartupFinishFragment()
            }

            return Fragment()
        }
    }
}
