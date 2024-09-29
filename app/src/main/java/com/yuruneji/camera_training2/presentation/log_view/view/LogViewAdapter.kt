package com.yuruneji.camera_training2.presentation.log_view.view

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 * @author toru
 * @version 1.0
 */
class LogViewAdapter : ListAdapter<LogViewItem, LogViewHolder>(TASKS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        return LogViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val repoItem = getItem(position)
        if (repoItem != null) {
            holder.bind(repoItem)
        }
    }

    companion object {
        private val TASKS_COMPARATOR = object : DiffUtil.ItemCallback<LogViewItem>() {
            override fun areItemsTheSame(oldItem: LogViewItem, newItem: LogViewItem): Boolean = oldItem == newItem
            override fun areContentsTheSame(oldItem: LogViewItem, newItem: LogViewItem): Boolean = oldItem.uid == newItem.uid
        }
    }
}
