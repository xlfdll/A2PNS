package org.xlfdll.a2pns

import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_app_list.*
import org.xlfdll.a2pns.adapters.AppListAdapter
import org.xlfdll.a2pns.helpers.AppHelper

class AppListActivity : AppCompatActivity() {
    private lateinit var installedPackages: MutableList<PackageInfo>
    private lateinit var selectedApps: MutableSet<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)

        setSupportActionBar(findViewById(R.id.actionToolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        this.title = getString(R.string.pref_title_action_bar_select_apps)

        selectedApps = HashSet()

        val selectAppSet =
            AppHelper.Settings.getStringSet(getString(R.string.pref_key_selected_apps), null)

        if (selectAppSet != null) {
            selectedApps.addAll(selectAppSet)
        }

        installedPackages = packageManager.getInstalledPackages(0)
        installedPackages.sortWith(compareBy { info ->
            info.applicationInfo.loadLabel(packageManager).toString()
        })

        appListRecyclerView.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this@AppListActivity)
            adapter = AppListAdapter(selectedApps, installedPackages)
        }
    }

    override fun onDestroy() {
        AppHelper.Settings.edit()
            .putStringSet(getString(R.string.pref_key_selected_apps), selectedApps)
            .commit()

        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_app_list, menu)

        var menuItem = menu?.findItem(R.id.search)
        val searchView = menuItem?.actionView as SearchView

        menuItem.setOnMenuItemClickListener {
            searchView.requestFocus()
        }

        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val trimmedNewText = newText.trim()

                    val packageInfo = installedPackages.find { info ->
                        info.applicationInfo.loadLabel(packageManager).toString().contains(
                            trimmedNewText,
                            true
                        ) || info.packageName.contains(trimmedNewText, true)
                    }

                    if (packageInfo != null) {
                        appListRecyclerView.scrollToPosition(installedPackages.indexOf(packageInfo))
                    }
                }

                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
        })

        menuItem = menu?.findItem(R.id.selectAll)
        menuItem?.setOnMenuItemClickListener {
            for (info in installedPackages) {
                if (!selectedApps.contains(info.packageName)) {
                    selectedApps.add(info.packageName)
                }
            }

            appListRecyclerView.adapter?.notifyDataSetChanged()

            true
        }

        menuItem = menu?.findItem(R.id.clearAll)
        menuItem?.isEnabled = selectedApps.size > 0
        menuItem?.setOnMenuItemClickListener {
            selectedApps.clear()

            appListRecyclerView.adapter?.notifyDataSetChanged()

            true
        }

        return super.onCreateOptionsMenu(menu)
    }
}