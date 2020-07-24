package ru.alexalekhin.todomanager.presentation.misc

import androidx.recyclerview.widget.DiffUtil

class CustomDiffUtilsCallback<T>(oldList: List<T>, newList: List<T>) : DiffUtil.Callback() {

    private val oldList = oldList.toList()
    private val newList = newList.toList()

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}
