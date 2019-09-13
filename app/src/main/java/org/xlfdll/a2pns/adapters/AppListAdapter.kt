package org.xlfdll.a2pns.adapters

import android.content.Context
import android.content.pm.PackageInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import org.xlfdll.a2pns.R

class AppListAdapter(
    private val selectedApps: MutableSet<String>,
    private val appList: List<PackageInfo>
) :
    RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {
    inner class AppListViewHolder(private val view: ConstraintLayout) :
        RecyclerView.ViewHolder(view)

    private lateinit var context: Context

    override fun getItemCount(): Int = appList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppListViewHolder {
        context = parent.context

        val itemView =
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_app_item,
                parent,
                false
            ) as ConstraintLayout

        return AppListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        holder.itemView.findViewById<ImageView>(R.id.appIconImageView)
            .setImageDrawable(appList[position].applicationInfo.loadIcon(context.packageManager))
        holder.itemView.findViewById<TextView>(R.id.appNameTextView).text =
            appList[position].applicationInfo.loadLabel(context.packageManager)
        holder.itemView.findViewById<TextView>(R.id.appPackageNameTextView).text =
            appList[position].packageName

        val appSelectedCheckBox =
            holder.itemView.findViewById<CheckBox>(R.id.appSelectedCheckBox)

        appSelectedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !selectedApps.contains(appList[position].packageName)) {
                selectedApps.add(appList[position].packageName)
            } else if (!isChecked) {
                selectedApps.remove(appList[position].packageName)
            }
        }
        appSelectedCheckBox.isChecked = selectedApps.contains(appList[position].packageName)
    }
}