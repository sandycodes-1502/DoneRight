package com.sandycodes.doneright.ui.addtask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sandycodes.doneright.data.local.Entity.TaskEntity
import com.sandycodes.doneright.data.local.database.DoneRightDatabase
import com.sandycodes.doneright.data.repository.TaskRepository
import com.sandycodes.doneright.databinding.FragmentAddTaskBinding
import kotlinx.coroutines.launch

class AddTaskBottomSheet : BottomSheetDialogFragment() {

    private var _binding : FragmentAddTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)

        val dao = DoneRightDatabase
            .getInstance(requireContext())
            .taskDao()

        val savebtn = binding.btnSave
        savebtn.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()

            if(title.isEmpty()) {
                binding.etTitle.error = "Requried"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                TaskRepository(dao).insertTask(
                    TaskEntity(
                        title = title,
                        description = description
                    )
                )
                dismiss()
            }

            Toast.makeText(this.context, "Task inserted", Toast.LENGTH_SHORT).show()

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
