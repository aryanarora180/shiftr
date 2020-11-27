package com.example.shiftr.view.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.shiftr.R
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.databinding.AddTodoFragmentBinding
import com.example.shiftr.view.showSnackbar
import com.example.shiftr.viewmodel.ToDoListViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddTodoBottomSheetFragment(private val viewModel: ToDoListViewModel) : BottomSheetDialogFragment() {

    private lateinit var binding: AddTodoFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddTodoFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            applyButton.setOnClickListener {
                viewModel.addTodo(
                    titleEdit.text.toString(),
                    descriptionEdit.text.toString(),
                    getSelectedColor(),
                )
            }
        }

        with(viewModel) {
            isAddingTodo.observe(viewLifecycleOwner, isAddingTodoObserver)
            onAddTodoErrorMessage.observe(viewLifecycleOwner, onAddTodoErrorMessageObserver)
            onAddTodoSuccess.observe(viewLifecycleOwner, onAddTodoSuccessObserver)
        }
    }

    private val isAddingTodoObserver = Observer<SingleLiveEvent<Boolean>> {
        it.getContentIfNotHandled()?.let { isAdding ->
            with(binding) {
                if (isAdding) {
                    titleLayout.isEnabled = false
                    descriptionLayout.isEnabled = false
                    colorChipGroup.isEnabled = false
                    applyButton.isEnabled = false
                    applyButton.text = ""
                    applyProgress.visibility = View.VISIBLE
                } else {
                    titleLayout.isEnabled = true
                    descriptionLayout.isEnabled = true
                    colorChipGroup.isEnabled = true
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

    private fun getSelectedColor(): String {
        return when (binding.colorChipGroup.checkedChipId) {
            R.id.chip_red -> "#D32F2F"
            R.id.chip_pink -> "#C2185B"
            R.id.chip_purple -> "#512DA8"
            R.id.chip_indigo -> "#303F9F"
            R.id.chip_blue -> "#1976D2"
            R.id.chip_cyan -> "#0097A7"
            R.id.chip_teal -> "#00796B"
            R.id.chip_green -> "#388E3C"
            R.id.chip_amber -> "#FFA000"
            R.id.chip_orange -> "#E64A19"
            R.id.chip_brown -> "#5D4037"
            R.id.chip_gray -> "#455A64"
            else -> "#D32F2F"
        }
    }
}