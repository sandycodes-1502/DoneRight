package com.sandycodes.doneright.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.sandycodes.doneright.R
import com.sandycodes.doneright.data.local.Entity.TaskEntity
import com.sandycodes.doneright.data.local.Entity.TaskStatus
import com.sandycodes.doneright.ui.model.HomeItem

class HomeAdapter(
    private val onStatusClick: (TaskEntity) -> Unit,
    private val onItemClick: (TaskEntity) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<HomeItem>()

    fun submitList(newItems: List<HomeItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HomeItem.Header -> VIEW_TYPE_HEADER
            is HomeItem.TaskItem -> VIEW_TYPE_TASK
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = inflater.inflate(
                    R.layout.item_section_header,
                    parent,
                    false
                )
                HeaderViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(
                    R.layout.item_task,
                    parent,
                    false
                )
                TaskViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HomeItem.Header ->
                (holder as HeaderViewHolder).bind(item)

            is HomeItem.TaskItem ->
                (holder as TaskViewHolder).bind(item.task)
        }
    }

    inner class HeaderViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val title = itemView.findViewById<TextView>(R.id.tvHeader)

        fun bind(header: HomeItem.Header) {
            title.text = header.title
        }
    }

    inner class TaskViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val title = itemView.findViewById<TextView>(R.id.taskTitle)
        private val chip = itemView.findViewById<Chip>(R.id.chipStatus)

        fun bind(task: TaskEntity) {
            title.text = task.title

            chip.text = when (task.status) {
                TaskStatus.TODO -> "To do"
                TaskStatus.IN_PROGRESS -> "In progress"
                TaskStatus.DONE -> "Completed"
            }

            val color = when (task.status) {
                TaskStatus.TODO -> R.color.status_todo
                TaskStatus.IN_PROGRESS -> R.color.status_in_progress
                TaskStatus.DONE -> R.color.status_completed
            }

            chip.setChipBackgroundColorResource(color)

            chip.setOnClickListener { onStatusClick(task) }
            itemView.setOnClickListener { onItemClick(task) }
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_TASK = 1
    }
}
