package ru.alexalekhin.todomanager.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task.view.*
import ru.alexalekhin.todomanager.R
import ru.alexalekhin.todomanager.data.task.DBTask
import ru.alexalekhin.todomanager.presentation.misc.CustomDiffUtilsCallback
import ru.alexalekhin.todomanager.presentation.misc.ItemTouchHelperAdapter
import java.util.*

class ProjectTaskAdapter(
    private val onCheck: (position: Int) -> Unit,
    private val onDismiss: (position: Int) -> Unit,
    private val onItemsReorder: (fromPos: Int, toPos: Int) -> Unit
) : RecyclerView.Adapter<ProjectTaskAdapter.ProjectTaskViewHolder>(), ItemTouchHelperAdapter {

    var tasks: List<DBTask> = emptyList()
        set(value) {
            val oldList = tasks
            val newList = value
            val callback = CustomDiffUtilsCallback(oldList, newList)
            field = value
            DiffUtil.calculateDiff(callback).dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectTaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_task, parent, false)
        return ProjectTaskViewHolder(itemView, onCheck)
    }

    override fun getItemCount() = tasks.size

    override fun onBindViewHolder(holder: ProjectTaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.checkBox.isChecked = task.checked
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(tasks, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo (toPosition + 1)) {
                Collections.swap(tasks, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        val weight1 = tasks[fromPosition].weight
        val weight2 = tasks[toPosition].weight

        tasks[fromPosition].weight = weight2
        tasks[toPosition].weight = weight1
        onItemsReorder(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        onDismiss(position)
    }


    class ProjectTaskViewHolder(
        itemView: View,
        private val onCheck: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val title: TextView = itemView.textViewTaskTitle
        val checkBox: CheckBox =
            itemView.checkBoxTaskMade.apply { setOnClickListener(this@ProjectTaskViewHolder) }

        override fun onClick(v: View?) {
            checkBox.isEnabled = false
            onCheck(bindingAdapterPosition)
        }
    }
}
