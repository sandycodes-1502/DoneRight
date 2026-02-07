package com.sandycodes.doneright.ui.home

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.sandycodes.doneright.R
import com.sandycodes.doneright.data.local.Entity.TaskStatus
import com.sandycodes.doneright.data.local.database.DoneRightDatabase
import com.sandycodes.doneright.data.repository.TaskRepository
import com.sandycodes.doneright.databinding.FragmentHomeBinding
import com.sandycodes.doneright.ui.addtask.AddTaskBottomSheet

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: TaskAdapter
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
//        return inflater.inflate(R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = DoneRightDatabase.getInstance(requireContext()).taskDao()
        val repository = TaskRepository(dao)
        viewModel = HomeViewModel(repository)

        adapter = TaskAdapter { task -> viewModel.updateTaskStatus(task)}

//        val recyclerView = view.findViewById<RecyclerView>(R.id.taskRecyclerView)
        val recyclerView = binding.taskRecyclerView
        recyclerView.adapter = adapter

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
            val emptyLayout = binding.emptyStateLayout

            if (tasks.isEmpty() || tasks.all { it.status == TaskStatus.DONE }) {
                recyclerView.visibility = View.GONE
                emptyLayout.visibility = View.VISIBLE
            } else {
                emptyLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        val addtaskbtn = binding.addtask

        addtaskbtn.setOnClickListener {
            AddTaskBottomSheet().show(parentFragmentManager, "AddTaskBottomSheet")
        }


    }

}