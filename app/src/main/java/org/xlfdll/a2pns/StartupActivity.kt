package org.xlfdll.a2pns

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_startup.*
import org.xlfdll.a2pns.fragments.StartupFinishFragment
import org.xlfdll.a2pns.fragments.StartupListenerPermissionFragment
import org.xlfdll.a2pns.fragments.StartupPairDevicesFragment
import org.xlfdll.a2pns.fragments.StartupSelectAppsFragment
import org.xlfdll.a2pns.helpers.AppHelper
import org.xlfdll.a2pns.helpers.AuthHelper

class StartupActivity : AppCompatActivity() {
    private var currentPageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)

        initAppFirstRun()

        startupViewPager.adapter = ViewPagerAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        startupViewPager.addOnPageChangeListener(ViewPagerChangeListener())
    }

    fun backAction(view: View) {
        if (currentPageIndex > 0) {
            startupViewPager.currentItem = currentPageIndex - 1
        }
    }

    fun nextAction(view: View) {
        if (currentPageIndex == 3) {
            finishAppFirstRun()
        } else if (currentPageIndex < 3) {
            startupViewPager.currentItem = currentPageIndex + 1
        }
    }

    fun showListenerPermissionNotGrantedToast() {
        Toast.makeText(
            this,
            this.getString(R.string.toast_startup_notification_listener_permission_not_granted),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    fun showDeviceNotPairedToast() {
        Toast.makeText(
            this,
            this.getString(R.string.toast_startup_device_not_paired),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    private fun initAppFirstRun() {
        // Update APNS authentication token
        AuthHelper.updateAPNSAuthToken(this)
    }

    private fun updateNavigateButtonStates() {
        backButton.isEnabled = (currentPageIndex > 0)
        nextButton.text =
            if (currentPageIndex < 3) {
                getString(R.string.button_startup_next)
            } else {
                getString(R.string.button_startup_finish)
            }
    }

    private fun finishAppFirstRun() {
        val isNotificationListenerEnabled = AppHelper.isNotificationListenerEnabled(this)
        val isDevicePaired = AppHelper.isDevicePaired(this)

        if (isNotificationListenerEnabled && isDevicePaired) {
            AppHelper.Settings.edit()
                .putBoolean(getString(R.string.pref_ns_key_is_first_run_done), true)
                .commit()

            finish()
        } else if (!isNotificationListenerEnabled) {
            showListenerPermissionNotGrantedToast()
        } else if (!isDevicePaired) {
            showDeviceNotPairedToast()
        }
    }

    inner class ViewPagerAdapter(fragmentManager: FragmentManager, behavior: Int) :
        FragmentPagerAdapter(fragmentManager, behavior) {
        override fun getCount(): Int {
            return 4
        }

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return StartupListenerPermissionFragment()
                1 -> return StartupPairDevicesFragment()
                2 -> return StartupSelectAppsFragment()
                3 -> return StartupFinishFragment()
            }

            return Fragment()
        }
    }

    inner class ViewPagerChangeListener : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            currentPageIndex = position

            updateNavigateButtonStates()
        }
    }
}
