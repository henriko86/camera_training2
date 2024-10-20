package com.yuruneji.camera_training.presentation.log_view.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yuruneji.camera_training.R
import com.yuruneji.camera_training.databinding.ListLogViewItemBinding

/**
 * @author toru
 * @version 1.0
 */
class LogViewHolder(
    private val binding: ListLogViewItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: LogViewItem) {
        setTextColor(item)

        binding.date.text = item.date
        binding.priority.text = getPriorityText(item.priority)
        // binding.tag.text = HtmlCompat.fromHtml("<font color='#00FF00'>${item.tag}</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.tag.text = item.tag
        binding.message.text = item.message

        // val sb = StringBuilder()
        // sb.append(item.date).append(" ")
        // sb.append(getFontTag(item.priority, getPriorityText(item.priority))).append(" ")
        // sb.append(getFontTag(item.priority, item.tag ?: "")).append(" ")
        // sb.append(getFontTag(item.priority, item.message))
        // binding.message.text = HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    // private fun getLogColor(priority: Int): Int {
    //     return when (priority) {
    //         Log.VERBOSE -> R.color.log_color_verbose
    //         Log.DEBUG -> R.color.log_color_debug
    //         Log.INFO -> R.color.log_color_info
    //         Log.WARN -> R.color.log_color_warn
    //         Log.ERROR -> R.color.log_color_error
    //         Log.ASSERT -> R.color.log_color_assert
    //         else -> 0
    //     }
    // }

    // private fun getFontTag(priority: Int, text: String): String {
    //     val sb = StringBuilder()
    //     sb.append("<font color='${getFontColor(getLogColor(priority))}'>").append(text).append("</font>")
    //     return sb.toString()
    // }

    // private fun getFontColor(context:Context,colorId: Int): String {
    //     // val str = "#" + Integer.toHexString(ContextCompat.getColor(context, colorId));
    //     return "#" + Integer.toHexString(ContextCompat.getColor(context, colorId) and 0x00ffffff)
    // }

    /**
     * レベルの文字列を取得する
     * @param priority
     * @return
     */
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

    /**
     * 文字列の色を設定
     * @param item
     */
    private fun setTextColor(item: LogViewItem) {
        val textColor = when (item.priority) {
            Log.VERBOSE -> R.color.log_color_verbose
            Log.DEBUG -> R.color.log_color_debug
            Log.INFO -> R.color.log_color_info
            Log.WARN -> R.color.log_color_warn
            Log.ERROR -> R.color.log_color_error
            Log.ASSERT -> R.color.log_color_assert
            else -> 0
        }

        if (textColor > 0) {
            // binding.date.setTextColor(ContextCompat.getColor(itemView.context, textColor))
            binding.priority.setTextColor(ContextCompat.getColor(itemView.context, textColor))
            // binding.tag.setTextColor(ContextCompat.getColor(itemView.context, textColor))
            // binding.message.setTextColor(ContextCompat.getColor(itemView.context, textColor))
        }
        // if (backgroundColor > 0) {
        //     binding.date.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColor))
        //     binding.priority.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColor))
        //     binding.tag.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColor))
        // }
    }

    companion object {
        fun create(parent: ViewGroup): LogViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_log_view_item, parent, false)
            val binding = ListLogViewItemBinding.bind(view)
            return LogViewHolder(binding)
        }
    }
}
