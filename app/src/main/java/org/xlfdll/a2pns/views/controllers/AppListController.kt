package org.xlfdll.a2pns.views.controllers

import com.airbnb.epoxy.TypedEpoxyController
import org.xlfdll.a2pns.models.PackageItem
import org.xlfdll.a2pns.viewPackageItem

class AppListController : TypedEpoxyController<List<PackageItem>>() {
    private var callback: PackageItemCallback? = null

    override fun buildModels(data: List<PackageItem>?) {
        data?.forEach { packageItem ->
            // Must be same as XML layout file name
            // e.g. viewPackageItem <-> view_package_item.xml
            viewPackageItem {
                // Must have unique ID for each model instance
                id(packageItem.fullQualifiedName)
                name(packageItem.name)
                fullQualifiedName(packageItem.fullQualifiedName)
                icon(packageItem.icon)
                selected(packageItem.isSelected)
                itemCheckedChangeListener { model, parentView, clickedView, isChecked, position ->
                    this@AppListController.callback?.onPackageItemChecked(packageItem, clickedView, isChecked)
                }
            }
        }
    }

    fun setPackageItemCallback(callback: PackageItemCallback) {
        this.callback = callback
    }
}