package com.sandycodes.doneright.ui.addtask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sandycodes.doneright.R
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sandycodes.doneright.data.local.Entity.TaskEntity
import com.sandycodes.doneright.data.local.database.DoneRightDatabase
import com.sandycodes.doneright.data.repository.TaskRepository
import com.sandycodes.doneright.databinding.FragmentAddTaskBinding
import kotlinx.coroutines.launch

class AddEditTaskBottomSheet(
    private val task: TaskEntity? = null
): BottomSheetDialogFragment() {

    private var _binding : FragmentAddTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        task?.let {
            val title = binding.etTitle
            val desc = binding.etDescription
            val btnsave = binding.btnSave

            val color = ContextCompat.getColor(requireContext(), R.color.white)

            title.setText(it.title)
            title.setTextColor(color)
            desc.setText(it.description)
            desc.setTextColor(color)
            btnsave.text = "Update Task"
        }

        binding.btnSave.setOnClickListener {
            saveTask()
        }
    }

    private fun saveTask() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (title.isEmpty()) {
            binding.etTitle.error = "Title required"
            return
        }

        lifecycleScope.launch {

            val dao = DoneRightDatabase
                .getInstance(requireContext())
                .taskDao()

            val repository = TaskRepository(dao)

            if (task == null) {

                repository.insertTask(
                    TaskEntity(
                        title = title,
                        description = description
                    )
                )
            } else {
                repository.updateTask(
                    task.copy(
                        title = title,
                        description = description,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
