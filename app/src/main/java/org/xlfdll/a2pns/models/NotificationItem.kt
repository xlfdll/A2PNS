package org.xlfdll.a2pns.models

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.service.notification.StatusBarNotification
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.xlfdll.a2pns.R
import org.xlfdll.a2pns.base.BundleExtensions.getSpannableString

@Entity(tableName = "Notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var title: String,
    var text: String,
    var source: String,
    var packageName: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        null,
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(this.title)
        dest?.writeString(this.text)
        dest?.writeString(this.source)
        dest?.writeString(this.packageName)
    }

    companion object CREATOR : Parcelable.Creator<NotificationItem> {
        override fun createFromParcel(parcel: Parcel): NotificationItem {
            return NotificationItem(parcel)
        }

        override fun newArray(size: Int): Array<NotificationItem?> {
            return arrayOfNulls(size)
        }

        fun create(context: Context, sbn: StatusBarNotification?): NotificationItem {
            val title: String = sbn?.notification?.extras?.getSpannableString("android.title")
                ?: context.getString(R.string.notification_unknown_title)
            val text: String = sbn?.notification?.extras?.getSpannableString("android.text")
                ?: context.getString(R.string.notification_unknown_text)
            val source: String =
                if (sbn != null) context.packageManager.getPackageInfo(
                    sbn.packageName,
                    0
                ).applicationInfo.loadLabel(
                    context.packageManager
                ).toString()
                else ""
            val packageName: String = sbn?.packageName ?: ""

            return NotificationItem(null, title, text, source, packageName)
        }
    }
}