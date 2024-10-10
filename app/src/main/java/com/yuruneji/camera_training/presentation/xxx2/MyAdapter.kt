package com.yuruneji.camera_training.presentation.xxx2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuruneji.camera_training.R
import com.yuruneji.camera_training.databinding.ListItem2Binding
import com.yuruneji.camera_training.databinding.ListItemBinding

/**
 * @author toru
 * @version 1.0
 */
class MyAdapter(private val myList: MutableList<MyData>): RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ListItem2Binding.bind(view)
        val txvName = binding.txvSample
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txvName.text = myList[position].name
    }

    override fun getItemCount(): Int {
        return myList.size
    }
}
