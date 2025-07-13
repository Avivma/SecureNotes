package com.example.securenotes.features.main.ui

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.securenotes.features.main.ui.model.UiNote
import com.example.securenotes.features.main.ui.state.MainIntention

class MainAdapterManager {
    private var _adapter: MainAdapter? = null
    fun getAdapter(): MainAdapter = _adapter!!

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPos = viewHolder.adapterPosition
            val toPos = target.adapterPosition
            getAdapter().swapItems(fromPos, toPos)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // Not used
        }

        override fun isLongPressDragEnabled() = false  // We control drag start manually
    }
    private val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
    private var _dragHandler: OnStartDragListener? = null
    private val dragHandler get() = _dragHandler!!

    private fun setDragHandler(create: Boolean) {
        _dragHandler = if (create) {
            object : OnStartDragListener {
                override fun onStartDrag(viewHolder: MainAdapter.NoteViewHolder) {
                    itemTouchHelper.startDrag(viewHolder)
                }
            }
        } else {
            null
        }
    }

    fun set(notes: List<UiNote>, recyclerView: RecyclerView, callback: (MainIntention) -> Unit) {
        setDragHandler(true)
        _adapter = MainAdapter(notes, callback, dragHandler)
        getAdapter().setHasStableIds(true)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun reset() {
        setDragHandler(false)
        itemTouchHelper.attachToRecyclerView(null)
        _adapter?.clear()
        _adapter = null
    }
}

interface OnStartDragListener {
    fun onStartDrag(viewHolder: MainAdapter.NoteViewHolder)
}