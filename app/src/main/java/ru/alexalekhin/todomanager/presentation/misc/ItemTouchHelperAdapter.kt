package ru.alexalekhin.todomanager.presentation.misc

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int) : Boolean
    fun onItemMoved(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
//    fun onItemsUpdate()
}