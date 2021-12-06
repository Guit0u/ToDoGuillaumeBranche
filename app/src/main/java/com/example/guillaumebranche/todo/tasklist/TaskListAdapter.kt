package com.example.guillaumebranche.todo.tasklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.guillaumebranche.todo.databinding.ItemTaskBinding

object TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
    // are they the same "entity" ? (usually same id)
    override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem.title == newItem.title
    // do they have the same data ? (content)
}

class TaskListAdapter(val listener: TaskListListener): ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffCallback){

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(task: Task) {
            binding.taskTitle.text = task.title
            binding.taskDesc.text = task.description

            binding.deleteButton.setOnClickListener { listener.onClickDelete(task) }
            binding.modifyButton.setOnClickListener { listener.onClickModify(task) }
            binding.root.rootView.setOnLongClickListener{ listener.onClickShare(task)}

            //binding.shareTaskBtn.setOnClickListener { listener.onClickShare(task) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        //val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context))
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}