package org.xlfdll.a2pns

import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_app_list.*
import org.xlfdll.a2pns.adapters.AppListAdapter
import org.xlfdll.a2pns.helpers.AppHelper

class AppListActivity : AppCompatActivity() {
    private lateinit var installedPackages: List<PackageInfo>
    private lateinit var displayedPackages: List<PackageInfo>
    private lateinit var selectedApps: MutableSet<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)

        setSupportActionBar(findViewById(R.id.actionToolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        this.title = getString(R.string.pref_title_action_bar_select_apps)

        selectedApps = collectSelectedApps()
        installedPackages = getInstalledPackages()
        displayedPackages = filterInstalledPackages(null, installedPackages)

        refreshAppListView()
    }

    override fun onDestroy() {
        AppHelper.Settings.edit()
            .putStringSet(getString(R.string.pref_key_selected_apps), selectedApps)
            .commit()

        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_app_list, menu)

        setSearchViewBehavior(menu?.findItem(R.id.search))
        setSelectAllBehavior(menu?.findItem(R.id.selectAll))
        setClearAllBehavior(menu?.findItem(R.id.clearAll))

        return super.onCreateOptionsMenu(menu)
    }

    private fun collectSelectedApps(): MutableSet<String> {
        val selectedApps = hashSetOf<String>()
        val selectAppSet =
            AppHelper.Settings.getStringSet(getString(R.string.pref_key_selected_apps), null)

        if (selectAppSet != null) {
            selectedApps.addAll(selectAppSet)
        }

        return selectedApps
    }

    private fun getInstalledPackages(): List<PackageInfo> {
        val installedPackages = packageManager.getInstalledPackages(0)

        installedPackages.sortWith(compareBy { info ->
            info.applicationInfo.loadLabel(packageManager).toString()
        })

        return installedPackages
    }

    private fun filterInstalledPackages(
        keyword: String?,
        installedPackages: List<PackageInfo>
    ): List<PackageInfo> {
        return if (keyword == null) {
            installedPackages
        } else {
            val trimmedKeyword = keyword.trim()

            installedPackages.filter { info ->
                info.applicationInfo.loadLabel(packageManager).toString().contains(
                    trimmedKeyword,
                    true
                ) || info.packageName.contains(trimmedKeyword, true)
            }
        }
    }

    private fun setSearchViewBehavior(menuItem: MenuItem?) {
        val searchView = menuItem?.actionView as SearchView

        menuItem.setOnMenuItemClickListener {
            searchView.requestFocus()
        }
        menuItem.setOnActionExpandListener(SearchViewExpandListener())

        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(SearchViewQueryListener())
    }

    private fun setSelectAllBehavior(menuItem: MenuItem?) {
        menuItem?.setOnMenuItemClickListener {
            for (info in displayedPackages) {
                if (!selectedApps.contains(info.packageName)) {
                    selectedApps.add(info.packageName)
                }
            }

            appListRecyclerView.adapter?.notifyDataSetChanged()

            true
        }
    }

    private fun setClearAllBehavior(menuItem: MenuItem?) {
        menuItem?.isEnabled = selectedApps.size > 0
        menuItem?.setOnMenuItemClickListener {
            selectedApps.clear()

            appListRecyclerView.adapter?.notifyDataSetChanged()

            true
        }
    }

    private fun refreshAppListView() {
        appListRecyclerView.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this@AppListActivity)
            adapter = AppListAdapter(selectedApps, displayedPackages)
        }
    }

    inner class SearchViewQueryListener : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String?): Boolean {
            displayedPackages = filterInstalledPackages(newText, installedPackages)

            refreshAppListView()

            return true
        }

        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }
    }

    inner class SearchViewExpandListener : MenuItem.OnActionExpandListener {
        override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
            return true
        }

        override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
            val searchView = item?.actionView as SearchView

            searchView.setQuery(null, true)

            return true
        }
    }
}