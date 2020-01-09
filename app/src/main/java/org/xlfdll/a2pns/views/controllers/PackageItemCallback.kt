package org.xlfdll.a2pns.views.controllers

import android.widget.CompoundButton
import org.xlfdll.a2pns.models.PackageItem

interface PackageItemCallback {
    fun onPackageItemChecked(
        packageItem: PackageItem,
        buttonView: CompoundButton,
        isSelected: Boolean
    )
}