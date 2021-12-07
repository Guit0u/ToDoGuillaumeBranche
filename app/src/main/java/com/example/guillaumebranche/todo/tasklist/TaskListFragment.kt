package com.example.guillaumebranche.todo.tasklist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guillaumebranche.todo.databinding.FragmentTaskListBinding
import com.example.guillaumebranche.todo.form.FormActivity
import com.example.guillaumebranche.todo.network.TasksRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {

    private var taskList = listOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2", description = "bonjoure"),
        Task(id = "id_3", title = "Task 3")
    )
    lateinit var adapter: TaskListAdapter

    private val adapterListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            taskList = taskList - task
            adapter.submitList(taskList)
        }

        override fun onClickModify(task: Task) {
            val intent = Intent(activity, FormActivity::class.java)
            intent.putExtra("task",task)
            formLauncher.launch(intent)
        }

        override fun onClickShare(task: Task): Boolean {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "My task " + task.title + " : " + task.description)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
            return true
        }
    }


        private var _binding: FragmentTaskListBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    private val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as? Task
        val oldTask = taskList.firstOrNull { it.id == task?.id }
        if (oldTask != null) taskList = taskList - oldTask
        if (task != null)     taskList = taskList + task
        adapter.submitList(taskList)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var taskNumber = 0
        var savedTask = savedInstanceState?.getSerializable("task_$taskNumber") as? Task

        if (savedTask != null) {
            taskList = listOf()
        }

        while (savedTask != null) {
            taskList = taskList + savedTask
            taskNumber++
            savedTask = savedInstanceState?.getSerializable("task_$taskNumber") as? Task
        }
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val tasksRepository = TasksRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = TaskListAdapter(adapterListener)
        binding.recyclerView.adapter = adapter
        adapter.submitList(taskList)
        binding.addButton.setOnClickListener {
            val intent = Intent(activity, FormActivity::class.java)
            formLauncher.launch(intent)
        }
        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            tasksRepository.taskList.collect { list ->
                adapter.submitList(list)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            tasksRepository.refresh() // on demande de rafraîchir les données sans attendre le retour directement
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        for((taskNumber, task) in taskList.withIndex()){
            outState.putSerializable("task_$taskNumber", task)
        }
    }
}




