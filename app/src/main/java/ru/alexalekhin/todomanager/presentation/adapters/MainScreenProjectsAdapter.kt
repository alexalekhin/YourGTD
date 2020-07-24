package ru.alexalekhin.todomanager.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_project.view.*

import ru.alexalekhin.todomanager.R
import ru.alexalekhin.todomanager.data.project.DBProject
import ru.alexalekhin.todomanager.presentation.misc.CustomDiffUtilsCallback
import ru.alexalekhin.todomanager.presentation.misc.ItemTouchHelperAdapter

import java.util.*

class MainScreenProjectsAdapter(
    private val onItemInteractionListener: OnItemInteractionListener
) :
    RecyclerView.Adapter<MainScreenProjectsAdapter.MainScreenProjectViewHolder>(),
    ItemTouchHelperAdapter {

    var projects: List<DBProject> = emptyList()
        set(value) {
            val oldList = projects
            val newList = value
            val callback = CustomDiffUtilsCallback(oldList, newList)
            field = value
            DiffUtil.calculateDiff(callback).dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainScreenProjectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_project, parent, false)
        return MainScreenProjectViewHolder(
            itemView,
            onItemInteractionListener
        )
    }

    override fun getItemCount() = projects.size

    override fun onBindViewHolder(holder: MainScreenProjectViewHolder, position: Int) {
        val folder = projects[position]
        with(holder) {
            title.text = folder.title
            image.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.ic_schedule_white_24dp))
        }
    }

    class MainScreenProjectViewHolder(
        itemView: View,
        private val onItemInteractionListener: OnItemInteractionListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val title: TextView = itemView.textViewProjectTitle
        val image: ImageView = itemView.imageViewProjectProgress

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemInteractionListener.onProjectClick(adapterPosition)
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(projects, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo (toPosition + 1)) {
                Collections.swap(projects, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        val weight1 = projects[fromPosition].weight
        val weight2 = projects[toPosition].weight

        projects[fromPosition].weight = weight2
        projects[toPosition].weight = weight1
        onItemInteractionListener.onItemsReorder(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        onItemInteractionListener.onDismiss(position)
    }

    interface OnItemInteractionListener {
        fun onProjectClick(position: Int)
        fun onItemsReorder(fromPos: Int, toPos: Int)
        fun onDismiss(position: Int)
    }
}