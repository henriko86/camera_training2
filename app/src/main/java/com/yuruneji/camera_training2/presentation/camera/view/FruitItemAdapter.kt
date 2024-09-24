package com.yuruneji.camera_training2.presentation.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

/**
 * @author toru
 * @version 1.0
 */
class FruitItemAdapter(
    context: Context,
    private val resource: Int,
    private val fruits: List<FruitItem>
) : ArrayAdapter<FruitItem>(context, resource, fruits) {
    // class FruitItemAdapter(context: Context, private val layoutResourceId: Int, private val fruits: List<FruitItem>) :
    //     ArrayAdapter<FruitItem>(context, layoutResourceId, fruits) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // val view = convertView ?: View.inflate(context, R.layout.list_item, null)as LayoutInflater
        val view = convertView
            ?: (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                resource,
                null
            )
        val item = fruits[position]
        // val imageView = view.findViewById<ImageView>(R.id.imageView)
        // val textView = view.findViewById<TextView>(R.id.fruitText)

        // imageView.setImageResource(item.image)
        // textView.text = item.name


        return super.getView(position, convertView, parent)
    }

}

data class FruitItem(val name: String, val image: Int)
