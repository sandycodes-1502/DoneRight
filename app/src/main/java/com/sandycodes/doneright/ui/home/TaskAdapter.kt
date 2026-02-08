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

class TaskAdapter(
    private val onStatusClick: (TaskEntity) -> Unit,
    private val onItemClick: (TaskEntity) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    companion object {
        private const val VIEW_TODO = 0
        private const val VIEW_IN_PROGRESS = 1
        private const val VIEW_DONE = 2
    }
    private val tasks = mutableListOf<TaskEntity>()

    fun submitList(list: List<TaskEntity>) {
        tasks.clear()
        tasks.addAll(list)
        notifyDataSetChanged()
    }

    fun getTaskAt(position: Int) : TaskEntity {
        return tasks[position]
    }

    override fun getItemCount() = tasks.size

    override fun getItemViewType(position: Int): Int {
        return when (tasks[position].status) {
            TaskStatus.TODO -> VIEW_TODO
            TaskStatus.IN_PROGRESS -> VIEW_IN_PROGRESS
            TaskStatus.DONE -> VIEW_DONE
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val view = when (viewType) {
            VIEW_TODO -> inflater.inflate(
                R.layout.item_task,
                parent,
                false
            )

            VIEW_IN_PROGRESS -> inflater.inflate(
                R.layout.item_task_in_progress,
                parent,
                false
            )

            VIEW_DONE -> inflater.inflate(
                R.layout.item_task_done,
                parent,
                false
            )

            else -> inflater.inflate(
                R.layout.item_task,
                parent,
                false
            )
        }

        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])

    }

    inner class TaskViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val title = itemView.findViewById<TextView>(R.id.taskTitle)
        private val chip = itemView.findViewById<Chip>(R.id.chipStatus)

        fun bind(task: TaskEntity) {
            title.text = task.title

            chip.text = when (task.status) {
                TaskStatus.TODO -> "TO DO"
                TaskStatus.IN_PROGRESS -> "In Progress"
                TaskStatus.DONE -> "DONE"
            }

            chip.setOnClickListener { onStatusClick(task) }
            itemView.setOnClickListener { onItemClick(task) }
        }
    }
}
