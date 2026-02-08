package com.sandycodes.doneright.ui.home

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sandycodes.doneright.R
import com.sandycodes.doneright.data.local.database.DoneRightDatabase
import com.sandycodes.doneright.data.remote.FirebaseGoogleAuthManager
import com.sandycodes.doneright.data.remote.FirebaseGoogleAuthManager.AuthResult.*
import com.sandycodes.doneright.data.repository.TaskRepository
import com.sandycodes.doneright.databinding.FragmentHomeBinding
import com.sandycodes.doneright.ui.addtask.AddEditTaskBottomSheet
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

        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val task = adapter.getTaskAt(position)

                    viewModel.deleteTask(task)

                    Snackbar.make(
                        binding.root, "Task Deleted", Snackbar.LENGTH_LONG,
                    ).setAction("UNDO") {
                        viewModel.insertTask(task)
                    }.show()
                }
            }
        )

        itemTouchHelper.attachToRecyclerView(binding.taskRecyclerView)

        val signinbtn = binding.signinbtn

        signinbtn.setOnClickListener {
            lifecycleScope.launch {
                FirebaseGoogleAuthManager.signIn(
                    requireContext(),
                    onResult = { result ->
                        when (result) {
                            LinkedAnonymous -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Signed in with Google",
                                    Toast.LENGTH_SHORT
                                ).show()

                                repository.syncFromFirestore()
                            }

                            SignedInExisting -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Signed in to existing account",
                                    Toast.LENGTH_SHORT
                                ).show()

                                lifecycleScope.launch {

                                    repository.clearLocalTasks()
                                    repository.syncFromFirestore()

                                }
                            }
                        }
                    },
                    onError = { error ->
                        Toast.makeText(
                            requireContext(),
                            "Sign in failed: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

            }
        }

        val addtaskbtn = binding.addtask

        addtaskbtn.setOnClickListener {
            AddEditTaskBottomSheet().show(parentFragmentManager, "AddEditTaskBottomSheet")
        }
    }
}
