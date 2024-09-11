package com.yuruneji.cameratraining2.domain.usecase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.yuruneji.cameratraining2.R

/**
 * @author toru
 * @version 1.0
 */
class LogViewAdapter(context: Context, items: List<LogViewItem>) :
    ArrayAdapter<LogViewItem>(context, 0, items) {

    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // レイアウトの設定
        var view = convertView
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.log_view_list_item, parent, false)
        }

        val date: TextView? = view?.findViewById(R.id.date)
        date?.text = getItem(position)?.date

        val priority: TextView? = view?.findViewById(R.id.priority)
        priority?.text = getItem(position)?.message

        val tag: TextView? = view?.findViewById(R.id.tag)
        tag?.text = getItem(position)?.tag

        val message: TextView? = view?.findViewById(R.id.message)
        message?.text = getItem(position)?.message

        return view!!
    }

}
