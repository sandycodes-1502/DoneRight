package com.sandycodes.doneright.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sandycodes.doneright.R
import com.sandycodes.doneright.data.local.database.DoneRightDatabase
import com.sandycodes.doneright.data.repository.TaskRepository
import com.sandycodes.doneright.databinding.FragmentHomeBinding
import com.sandycodes.doneright.ui.addEditTask.AddEditTaskBottomSheet

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: TaskAdapter
    private var isLoading = true
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

        val cm = requireContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val isOnline = cm.activeNetworkInfo?.isConnected == true

        if (!isOnline) {
            Toast.makeText(
                requireContext(),
                "Offline mode — changes will sync later",
                Toast.LENGTH_SHORT
            ).show()
        }

        val dao = DoneRightDatabase.getInstance(requireContext()).taskDao()
        val repository = TaskRepository(requireContext(), dao)
        viewModel = HomeViewModel(repository)

        adapter = TaskAdapter( onStatusClick = { task ->
            viewModel.updateTaskStatus(task)
            requireContext().vibrateShort()

        }, onItemClick = { task ->
            AddEditTaskBottomSheet(task).show(parentFragmentManager, "AddEditTaskBottomSheet")
            }
        )

        val recyclerView = binding.taskRecyclerView
        val emptyLayout = binding.emptyLayout
        val homescreentitle = binding.homescreentitle
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            Log.d("TASK_LIST", "Received ${tasks.size} tasks")

            if (tasks.isEmpty() && !isLoading) {
                recyclerView.visibility = View.GONE
                homescreentitle.visibility = View.GONE
                emptyLayout.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                homescreentitle.visibility = View.VISIBLE
                emptyLayout.visibility = View.GONE
            }
            adapter.submitList(tasks)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading = it
            binding.progressBar.visibility =
                if(it) View.VISIBLE else View.GONE
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
                    val position = viewHolder.absoluteAdapterPosition
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

        val addtaskbtn = binding.addtask

        addtaskbtn.setOnClickListener {
            AddEditTaskBottomSheet().show(parentFragmentManager, "AddEditTaskBottomSheet")
        }
    }

    fun Context.vibrateShort() {
        val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            val effect = VibrationEffect.createOneShot(
                50,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
            vibrator.vibrate(effect)
        }
    }

}
