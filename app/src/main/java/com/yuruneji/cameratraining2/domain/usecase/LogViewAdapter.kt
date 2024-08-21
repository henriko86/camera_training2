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
class LogViewAdapter(context: Context, items: List<ListViewItem>) :
    ArrayAdapter<ListViewItem>(context, 0,items) {

    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // val view = super.getView(position, convertView, parent)
        // レイアウトの設定
        var view = convertView
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.samplelist_item, parent, false)
        }

        // val title: TextView? = view?.findViewById(R.id.title)
        // title?.text = getItem(position)?.title

        val body: TextView? = view?.findViewById(R.id.body)
        body?.text = getItem(position)?.body

        return view!!
    }

}
