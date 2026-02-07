package com.sandycodes.doneright.ui.home

//import android.R
import com.sandycodes.doneright.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.sandycodes.doneright.data.local.Entity.TaskEntity
import com.sandycodes.doneright.data.local.Entity.TaskStatus


class TaskAdapter(private val onStatusClick: (TaskEntity) -> Unit)
    : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks = emptyList<TaskEntity>()

    fun submitList(list: List<TaskEntity>) {
        tasks = list
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.taskTitle)
        val chip: Chip = itemView.findViewById(R.id.chipStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.chip.text = statusText(task.status)

        val color = when (task.status) {
            TaskStatus.TODO -> R.color.status_todo
            TaskStatus.IN_PROGRESS -> R.color.status_in_progress
            TaskStatus.DONE -> R.color.status_completed
        }

        holder.chip.setChipBackgroundColorResource(color)

        holder.chip.setOnClickListener {
            onStatusClick(task)
        }

        holder.chip.setOnClickListener {
            it.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(80)
                .withEndAction {
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(80)
                        .start()
                }.start()

            onStatusClick(task)
        }


    }

    private fun statusText(status: TaskStatus): String {
        return when (status) {
            TaskStatus.TODO -> "To do"
            TaskStatus.IN_PROGRESS -> "In progress"
            TaskStatus.DONE -> "Done"
        }
    }


    override fun getItemCount() = tasks.size
}
