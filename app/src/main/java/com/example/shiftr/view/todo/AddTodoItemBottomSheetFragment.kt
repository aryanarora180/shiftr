package com.example.shiftr.view.todo

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import com.example.shiftr.R
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.data.TodoItem
import com.example.shiftr.databinding.AddTodoItemFragmentBinding
import com.example.shiftr.view.showSnackbar
import com.example.shiftr.viewmodel.ViewTodoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker

class AddTodoItemBottomSheetFragment(private val viewModel: ViewTodoViewModel) :
    BottomSheetDialogFragment() {

    private lateinit var binding: AddTodoItemFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddTodoItemFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    private val getFile =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            viewModel.selectedUri = uri
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            dateLayout.setEndIconOnClickListener {
                showDatePicker()
            }
            timeLayout.setEndIconOnClickListener {
                showTimePicker()
            }

            attachFileButton.setOnClickListener {
                getFile.launch("application/*")
            }

            applyButton.setOnClickListener {
                viewModel.addTodoItem(
                    todoEdit.text.toString(),
                    getSelectedPriority(),
                    dateEdit.text.toString(),
                    timeEdit.text.toString(),
                )
            }
        }

        with(viewModel) {
            isAddingTodoItem.observe(viewLifecycleOwner, isAddingTodoObserver)
            onAddTodoItemErrorMessage.observe(viewLifecycleOwner, onAddTodoErrorMessageObserver)
            onAddTodoItemSuccess.observe(viewLifecycleOwner, onAddTodoSuccessObserver)
            fileDetails.observe(viewLifecycleOwner, fileDetailsObserver)
        }
    }

    private val isAddingTodoObserver = Observer<SingleLiveEvent<Boolean>> {
        it.getContentIfNotHandled()?.let { isAdding ->
            with(binding) {
                if (isAdding) {
                    todoLayout.isEnabled = false
                    dateLayout.isEnabled = false
                    timeLayout.isEnabled = false
                    applyButton.isEnabled = false
                    applyButton.text = ""
                    applyProgress.visibility = View.VISIBLE
                } else {
                    todoLayout.isEnabled = true
                    dateLayout.isEnabled = true
                    timeLayout.isEnabled = true
                    applyButton.isEnabled = true
                    applyButton.text = getString(R.string.add)
                    applyProgress.visibility = View.GONE
                }
            }
        }
    }

    private val onAddTodoErrorMessageObserver = Observer<SingleLiveEvent<String>> {
        requireView().showSnackbar(it)
    }

    private val onAddTodoSuccessObserver = Observer<SingleLiveEvent<Boolean>> {
        it.getContentIfNotHandled()?.let { isSuccess ->
            if (isSuccess) {
                dismiss()
            }
        }
    }

    private val fileDetailsObserver = Observer<String> {
        binding.fileDetailsText.text = it
    }

    private fun showDatePicker() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker().build()
        datePicker.addOnPositiveButtonClickListener {
            binding.dateEdit.setText(viewModel.formatDate(it))
        }
        datePicker.show(parentFragmentManager, datePicker.toString())
    }

    private fun showTimePicker() {
        val timePicker = MaterialTimePicker.Builder().build()
        timePicker.addOnPositiveButtonClickListener {
            binding.timeEdit.setText(viewModel.formatTime(timePicker.hour, timePicker.minute))
        }
        timePicker.show(parentFragmentManager, timePicker.toString())
    }

    private fun getSelectedPriority() = when (binding.priorityChipGroup.checkedChipId) {
        R.id.chip_low -> TodoItem.PRIORITY_LOW
        R.id.chip_imp -> TodoItem.PRIORITY_IMPORTANT
        R.id.chip_urg -> TodoItem.PRIORITY_URGENT
        else -> TodoItem.PRIORITY_LOW
    }
}