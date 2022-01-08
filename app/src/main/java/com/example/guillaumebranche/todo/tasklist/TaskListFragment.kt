package com.example.guillaumebranche.todo.tasklist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.example.guillaumebranche.todo.databinding.FragmentTaskListBinding
import com.example.guillaumebranche.todo.form.FormActivity
import com.example.guillaumebranche.todo.network.Api
import com.example.guillaumebranche.todo.user.UserInfoActivity
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {

   /* private var taskList = listOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2", description = "bonjoure"),
        Task(id = "id_3", title = "Task 3")
    )*/
    lateinit var adapter: TaskListAdapter
    private val viewModel: TaskListViewModel by viewModels()


    private val adapterListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            viewModel.deleteTask(task)
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
        val oldTask = viewModel.taskList.value.firstOrNull { it.id == task?.id }
        if (task != null){
            if(oldTask != null)
                viewModel.editTask(task)
            else
                viewModel.addTask(task)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    //private val tasksRepository = TasksRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = TaskListAdapter(adapterListener)
        binding.recyclerView.adapter = adapter
        binding.addButton.setOnClickListener {
            val intent = Intent(activity, FormActivity::class.java)
            formLauncher.launch(intent)
        }
        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.taskList.collect { newList ->
                adapter.submitList(newList)
            }
        }

        viewModel.loadTasks()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.loadTasks() // on demande de rafraîchir les données sans attendre le retour directement
            val userInfo = Api.userWebService.getInfo().body()

            binding.avatar.load(userInfo?.avatar){
                transformations(CircleCropTransformation())
            }

            binding.avatar.setOnClickListener{
                val intent = Intent(activity, UserInfoActivity::class.java)
                startActivity(intent)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




