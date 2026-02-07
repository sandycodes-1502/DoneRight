package com.sandycodes.doneright.ui.home

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sandycodes.doneright.R
import com.sandycodes.doneright.data.local.Entity.TaskStatus
import com.sandycodes.doneright.data.local.database.DoneRightDatabase
import com.sandycodes.doneright.data.repository.TaskRepository
import com.sandycodes.doneright.databinding.FragmentHomeBinding
import com.sandycodes.doneright.ui.addtask.AddEditTaskBottomSheet
import com.sandycodes.doneright.ui.model.HomeItem

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HomeAdapter
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = DoneRightDatabase.getInstance(requireContext()).taskDao()
        val repository = TaskRepository(dao)
        viewModel = HomeViewModel(repository)

        adapter = HomeAdapter(
            onStatusClick = { task ->
                viewModel.updateTaskStatus(task)
            },
            onItemClick = { task ->
                AddEditTaskBottomSheet(task)
                    .show(parentFragmentManager, "EditTask")
            }
        )

        val recyclerView = binding.taskRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.homeItems.observe((viewLifecycleOwner)) { items ->
            adapter.submitList(items)

            val hasAnyTaskItem = items.any {it is HomeItem.TaskItem}

            binding.taskRecyclerView.visibility =
                if (hasAnyTaskItem) View.VISIBLE else View.GONE

            binding.emptyStateLayout.visibility =
                if (hasAnyTaskItem) View.GONE else View.VISIBLE
        }
        binding.addtask.setOnClickListener {
            AddEditTaskBottomSheet()
                .show(parentFragmentManager, "AddEditTaskBottomSheet")
        }

//        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
//            Log.d("TASK_LIST", "Recieved ${tasks.size} tasks")
//            tasks.forEach {
//                Log.d("TASK_LIST", "${it.title} -> ${it.status}")
//            }
//            adapter.submitList(tasks)
//            val emptyLayout = binding.emptyStateLayout
//
//            if (tasks.isEmpty() || tasks.all { it.status == TaskStatus.DONE }) {
//                recyclerView.visibility = View.GONE
//                emptyLayout.visibility = View.VISIBLE
//            } else {
//                emptyLayout.visibility = View.GONE
//                recyclerView.visibility = View.VISIBLE
//            }
//        }
//
//        viewModel.tasks.observe(viewLifecycleOwner) {
//            adapter.submitList(it)
//        }
//
//        val addtaskbtn = binding.addtask
//
//        addtaskbtn.setOnClickListener {
//            AddEditTaskBottomSheet().show(parentFragmentManager, "AddEditTaskBottomSheet")
//        }


    }

}