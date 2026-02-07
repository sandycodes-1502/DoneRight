package com.sandycodes.doneright.ui.home

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.sandycodes.doneright.R
import com.sandycodes.doneright.data.local.Entity.TaskEntity
import com.sandycodes.doneright.data.local.database.DoneRightDatabase
import com.sandycodes.doneright.data.repository.TaskRepository
import com.sandycodes.doneright.databinding.FragmentHomeBinding
import com.sandycodes.doneright.ui.addtask.AddTaskBottomSheet
import kotlinx.coroutines.launch

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
        adapter = TaskAdapter()

//        val recyclerView = view.findViewById<RecyclerView>(R.id.taskRecyclerView)
        val recyclerView = binding.taskRecyclerView
        recyclerView.adapter = adapter

        val dao = DoneRightDatabase.getInstance(requireContext()).taskDao()
        val repository = TaskRepository(dao)
        viewModel = HomeViewModel(repository)

        viewModel.tasks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        val addtaskbtn = binding.addtask

        addtaskbtn.setOnClickListener {
            lifecycleScope.launch {

                // Insert test task
                repository.insertTask(
                    TaskEntity(
                        title = "Room Test",
                        description = "If you see this, Room works"
                    )
                )

            }

            AddTaskBottomSheet().show(parentFragmentManager, "AddTaskBottomSheet")

        }


    }

}