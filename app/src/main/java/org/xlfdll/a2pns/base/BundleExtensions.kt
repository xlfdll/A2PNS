package org.xlfdll.a2pns.base

import android.os.Bundle
import android.text.SpannableString

object BundleExtensions {
    fun Bundle?.getSpannableString(key: String): String? {
        if (this?.containsKey(key) == true) {
            val data = this.get(key)

            if (data is String || data is SpannableString) {
                return data.toString()
            }
        }

        return null
    }
}