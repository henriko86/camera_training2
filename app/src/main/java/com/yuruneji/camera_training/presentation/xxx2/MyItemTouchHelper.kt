package com.yuruneji.camera_training.presentation.xxx2

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * @author toru
 * @version 1.0
 */
class MyItemTouchHelper {
    companion object {
        fun getCallback(mutableList: MutableList<*>) =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
            {
                // For up and down dragging.
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition
                    @Suppress("UNCHECKED_CAST")
                    val list = mutableList as MutableList<Any>

                    val tempData = list[fromPosition]
                    list.removeAt(fromPosition)
                    list.add(toPosition, tempData)
                    recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
                    return true
                }

                // For swiping left and right. Not used in this case. (swipeDirs = 0)
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                // Selected row is highlighted.
                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)

                    when (actionState) {
                        ItemTouchHelper.ACTION_STATE_DRAG -> {
                            viewHolder?.itemView?.alpha = 0.6f
                        }
                    }
                }

                // The row is not highlighted when unselected.
                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)

                    viewHolder.itemView.alpha = 1.0f
                }
            }
    }
}
