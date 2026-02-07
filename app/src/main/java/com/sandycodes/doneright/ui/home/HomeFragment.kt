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
    private lateinit var adapter: TaskAdapter
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

        adapter = TaskAdapter( onStatusClick = { task ->
            viewModel.updateTaskStatus(task)
        }, onItemClick = { task ->
            AddEditTaskBottomSheet(task).show(parentFragmentManager, "AddEditTaskBottomSheet")
            }
        )

        val recyclerView = binding.taskRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            Log.d("TASK_LIST", "Received ${tasks.size} tasks")

            adapter.submitList(tasks)
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        val addtaskbtn = binding.addtask

        addtaskbtn.setOnClickListener {
            AddEditTaskBottomSheet().show(parentFragmentManager, "AddEditTaskBottomSheet")
        }
    }
}