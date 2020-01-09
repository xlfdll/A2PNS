package org.xlfdll.a2pns.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.models.PackageItem
import javax.inject.Inject

class AppListViewModel @Inject constructor(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private lateinit var installedPackages: List<PackageItem>
    private val selectedApps: MutableSet<String> = hashSetOf()

    private val mutableDisplayedPackageLiveView: MutableLiveData<List<PackageItem>> by lazy {
        MutableLiveData<List<PackageItem>>(listOf())
    }
    private val mutableSelectedAppLiveView: MutableLiveData<Set<String>> by lazy {
        MutableLiveData<Set<String>>(selectedApps)
    }

    val displayedPackageLiveView: LiveData<List<PackageItem>> = mutableDisplayedPackageLiveView
    val selectedAppLiveView: LiveData<Set<String>> = mutableSelectedAppLiveView

    init {
        viewModelScope.launch {
            installedPackages = context.packageManager.getInstalledPackages(0).map { info ->
                PackageItem(info, context.packageManager)
            }.sortedWith(compareBy { item ->
                item.name
            })

            val selectedAppPreferenceSet = sharedPreferences.getStringSet(
                context.getString(R.string.pref_key_selected_apps),
                null
            )

            if (selectedAppPreferenceSet != null) {
                selectedApps.addAll(selectedAppPreferenceSet)
            }

            installedPackages.filter { packageItem ->
                selectedApps.contains(packageItem.fullQualifiedName)
            }.map { packageItem ->
                packageItem.isSelected = true
            }
        }
    }

    fun filterInstalledPackages(keyword: String?) {
        viewModelScope.launch {
            val packagesToDisplay = if (keyword == null) {
                installedPackages
            } else {
                val trimmedKeyword = keyword.trim()

                installedPackages.filter { item ->
                    item.name.contains(
                        trimmedKeyword,
                        true
                    ) || item.fullQualifiedName.contains(trimmedKeyword, true)
                }
            }

            mutableDisplayedPackageLiveView.value = packagesToDisplay
        }
    }

    fun selectApps(packageList: List<PackageItem>) {
        viewModelScope.launch {
            packageList.filter { packageItem ->
                !selectedApps.contains(packageItem.fullQualifiedName)
            }.map { packageItem ->
                selectedApps.add(packageItem.fullQualifiedName)

                packageItem.isSelected = true
            }

            mutableSelectedAppLiveView.value = selectedApps
        }
    }

    fun selectAllApps() {
        selectApps(installedPackages)
    }

    fun removeSelectedApp(packageItem: PackageItem) {
        viewModelScope.launch {
            if (selectedApps.contains(packageItem.fullQualifiedName)) {
                selectedApps.remove(packageItem.fullQualifiedName)

                packageItem.isSelected = false
            }

            mutableSelectedAppLiveView.value = selectedApps
        }
    }

    fun clearSelectedApps() {
        viewModelScope.launch {
            selectedApps.clear()

            installedPackages.map { packageItem ->
                packageItem.isSelected = false
            }

            mutableSelectedAppLiveView.value = selectedApps
        }
    }

    fun saveSelectedApps() {
        sharedPreferences.edit()
            .putStringSet(context.getString(R.string.pref_key_selected_apps), selectedApps)
            .apply()
    }
}