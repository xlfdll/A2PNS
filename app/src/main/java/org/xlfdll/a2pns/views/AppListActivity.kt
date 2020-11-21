package org.xlfdll.a2pns.views

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airbnb.epoxy.EpoxyRecyclerView
import dagger.android.support.DaggerAppCompatActivity
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.base.ViewModelFactory
import org.xlfdll.a2pns.models.PackageItem
import org.xlfdll.a2pns.viewmodels.AppListViewModel
import org.xlfdll.a2pns.views.controllers.AppListController
import org.xlfdll.a2pns.views.controllers.PackageItemCallback
import javax.inject.Inject

class AppListActivity : DaggerAppCompatActivity(), PackageItemCallback {
    @Inject
    lateinit var appListController: AppListController

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var appListViewModel: AppListViewModel
    private var optionMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)
        setSupportActionBar(findViewById(R.id.actionToolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val appListRecyclerView = findViewById<EpoxyRecyclerView>(R.id.appListRecyclerView)

        this.title = getString(R.string.pref_title_action_bar_select_apps)

        appListViewModel = ViewModelProvider(this, viewModelFactory)[AppListViewModel::class.java]

        appListController.setPackageItemCallback(this)
        appListController.addModelBuildListener {
            progressBar.visibility = View.INVISIBLE
        }
        appListRecyclerView.setController(appListController)

        appListViewModel.displayedPackageLiveView.observe(this, Observer { packages ->
            appListController.setData(packages)
        })
        appListViewModel.selectedAppLiveView.observe(this, Observer {
            appListController.setData(appListViewModel.displayedPackageLiveView.value)

            optionMenu?.findItem(R.id.clearAll)?.isEnabled =
                appListViewModel.selectedAppLiveView.value!!.isNotEmpty()
        })

        appListViewModel.filterInstalledPackages(null)
    }

    override fun onDestroy() {
        appListViewModel.saveSelectedApps()

        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_app_list, menu)

        setSearchViewBehavior(menu?.findItem(R.id.search))
        setSelectAllBehavior(menu?.findItem(R.id.selectAll))
        setClearAllBehavior(menu?.findItem(R.id.clearAll))

        optionMenu = menu

        return super.onCreateOptionsMenu(menu)
    }

    override fun onPackageItemChecked(
        packageItem: PackageItem,
        buttonView: CompoundButton,
        isSelected: Boolean
    ) {
        if (isSelected) {
            appListViewModel.selectApps(listOf(packageItem))
        } else {
            appListViewModel.removeSelectedApp(packageItem)
        }

        packageItem.isSelected = isSelected
    }

    private fun setSearchViewBehavior(menuItem: MenuItem?) {
        val searchView = menuItem?.actionView as SearchView

        menuItem.setOnMenuItemClickListener {
            searchView.requestFocus()
        }
        menuItem.setOnActionExpandListener(SearchViewExpandListener())

        searchView.isIconifiedByDefault = false
        searchView.queryHint = getString(R.string.hint_search)
        searchView.setOnQueryTextListener(SearchViewQueryListener())
    }

    private fun setSelectAllBehavior(menuItem: MenuItem?) {
        menuItem?.setOnMenuItemClickListener {
            appListViewModel.selectAllApps()

            true
        }
    }

    private fun setClearAllBehavior(menuItem: MenuItem?) {
        menuItem?.isEnabled = appListViewModel.selectedAppLiveView.value!!.isNotEmpty()
        menuItem?.setOnMenuItemClickListener {
            appListViewModel.clearSelectedApps()

            true
        }
    }

    inner class SearchViewQueryListener : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String?): Boolean {
            appListViewModel.filterInstalledPackages(newText)

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