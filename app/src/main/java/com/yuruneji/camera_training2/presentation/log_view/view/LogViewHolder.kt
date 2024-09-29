package com.yuruneji.camera_training2.presentation.log_view.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yuruneji.camera_training2.R
import com.yuruneji.camera_training2.databinding.LogViewListItemBinding

/**
 * @author toru
 * @version 1.0
 */
class LogViewHolder(
    private val binding: LogViewListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: LogViewItem) {
        setPriority(item)

        binding.date.text = item.date
        binding.priority.text = getPriorityText(item.priority)
        binding.tag.text = item.tag
        binding.message.text = item.message
    }

    private fun getPriorityText(priority: Int): String {
        return when (priority) {
            Log.VERBOSE -> "VERBOSE"
            Log.DEBUG -> "DEBUG"
            Log.INFO -> "INFO"
            Log.WARN -> "WARN"
            Log.ERROR -> "ERROR"
            Log.ASSERT -> "ASSERT"
            else -> ""
        }
    }

    private fun setPriority(item: LogViewItem) {
        val textColor = when (item.priority) {
            Log.VERBOSE -> R.color.log_color_verbose
            Log.DEBUG -> R.color.log_color_debug
            Log.INFO -> R.color.log_color_info
            Log.WARN -> R.color.log_color_warn
            Log.ERROR -> R.color.log_color_error
            Log.ASSERT -> R.color.log_color_assert
            else -> 0
        }
        // val backgroundColor = when (item.priority) {
        //     Log.DEBUG -> R.color.log_color_debug
        //     Log.INFO -> R.color.log_color_info
        //     Log.ASSERT -> R.color.log_color_assert
        //     else -> 0
        // }

        if (textColor > 0) {
            binding.date.setTextColor(ContextCompat.getColor(itemView.context, textColor))
            binding.priority.setTextColor(ContextCompat.getColor(itemView.context, textColor))
            binding.tag.setTextColor(ContextCompat.getColor(itemView.context, textColor))
        }
        // if (backgroundColor > 0) {
        //     binding.date.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColor))
        //     binding.priority.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColor))
        //     binding.tag.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColor))
        // }
    }

    companion object {
        fun create(parent: ViewGroup): LogViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.log_view_list_item, parent, false)
            val binding = LogViewListItemBinding.bind(view)
            return LogViewHolder(binding)
        }
    }
}
