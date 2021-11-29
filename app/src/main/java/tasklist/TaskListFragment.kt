package tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.guillaumebranche.todo.R
import com.example.guillaumebranche.todo.databinding.FragmentTaskListBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class TaskListFragment : Fragment() {

    private var taskList = listOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )

    private var _binding: FragmentTaskListBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater,container,false)
        //val rootView = inflater.inflate(R.layout.fragment_task_list,container,false)
        val rootView = binding.root
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = TaskListAdapter()
        recyclerView.adapter = adapter
        adapter.submitList(taskList)
        val button = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        button.setOnClickListener {
            val newTask = Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}")
            taskList = taskList + newTask
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

object TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        // are they the same "entity" ? (usually same id)
    override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem.title == newItem.title
    // do they have the same data ? (content)
}

class TaskListAdapter() : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffCallback){

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(taskTitle:String,taskDescription: String){
            val taskTitleView = itemView.findViewById<TextView>(R.id.task_title)
            val taskDescView = itemView.findViewById<TextView>(R.id.task_desc)
            taskTitleView.text = taskTitle
            taskDescView.text = taskDescription
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(currentList[position].title,currentList[position].description)
    }

}

