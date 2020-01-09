package org.xlfdll.a2pns.models

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

class PackageItem(packageInfo: PackageInfo, packageManager: PackageManager) {
    val name: String = packageInfo.applicationInfo.loadLabel(packageManager).toString()
    val fullQualifiedName: String = packageInfo.packageName
    val icon: Drawable = packageInfo.applicationInfo.loadIcon(packageManager)
    var isSelected: Boolean = false
}