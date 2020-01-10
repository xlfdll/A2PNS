package org.xlfdll.a2pns.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.base.ViewModelFactory
import org.xlfdll.a2pns.helpers.ViewHelper
import org.xlfdll.a2pns.viewmodels.ServiceViewModel
import javax.inject.Inject

class SettingsActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var serviceViewModel: ServiceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.actionToolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        this.title = getString(R.string.action_settings)

        serviceViewModel =
            ViewModelProviders.of(this, viewModelFactory)[ServiceViewModel::class.java]

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.settings,
                SettingsFragment()
            )
            .commit()
    }

    // Preference fragment class must be a normal class instead of inner one
    class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {
        @Inject
        lateinit var serviceViewModel: ServiceViewModel

        override fun onCreate(savedInstanceState: Bundle?) {
            // Use AndroidSupportInjection here, as AndroidInjection uses old (v4) Fragment class
            AndroidSupportInjection.inject(this)

            super.onCreate(savedInstanceState)
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            initializePreferenceButtons()
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            val cachedNotificationCountKey = getString(R.string.pref_key_cached_notification_count)
            val customAuthTokenURLKey = getString(R.string.pref_key_custom_auth_token_url)

            when (key) {
                cachedNotificationCountKey -> {
                    try {
                        // Stupid EditTextPreference always save integers as strings
                        sharedPreferences?.getString(key, null)?.toInt()
                    } catch (ex: Throwable) {
                        sharedPreferences?.edit()
                            ?.putString(key, "50")
                            ?.apply()
                    }
                }
                customAuthTokenURLKey -> {
                    if (sharedPreferences?.getString(key, null) == "") {
                        sharedPreferences.edit()
                            .putString(key, null)
                            .apply()
                    }
                }
            }
        }

        override fun onResume() {
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

            super.onResume()
        }

        override fun onPause() {
            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

            super.onPause()
        }

        private fun initializePreferenceButtons() {
            val context = requireContext()

            findPreference<Preference>(getString(R.string.pref_ns_key_sync_auth_token))?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    syncAuthToken(context)

                    true
                }
            findPreference<Preference>(getString(R.string.pref_ns_key_pair_device))?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    startActivity(Intent(context, QRCodeActivity::class.java))

                    true
                }
            findPreference<Preference>(getString(R.string.pref_ns_key_select_apps))?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    startActivity(Intent(context, AppListActivity::class.java))

                    true
                }
        }

        private fun syncAuthToken(context: Context) {
            MainScope().launch {
                try {
                    if (serviceViewModel.checkAuthTokenExpiration()) {
                        try {
                            serviceViewModel.updateAuthToken()

                            ViewHelper.showAuthTokenUpdatedToast(context)
                        } catch (ex: Throwable) {
                            ViewHelper.showAuthTokenErrorAlert(context)
                        }
                    } else {
                        ViewHelper.showAuthTokenLatestToast(context)
                    }
                } catch (ex: IllegalStateException) {
                    ViewHelper.showIncorrectClockErrorAlert(context)
                }
            }
        }
    }
}