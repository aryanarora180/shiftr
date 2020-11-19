package com.example.shiftr.view.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.databinding.AddTodoFragmentBinding
import com.example.shiftr.view.showSnackbar
import com.example.shiftr.viewmodel.ToDoListViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddTodoBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: AddTodoFragmentBinding
    private val viewModel by activityViewModels<ToDoListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                    applyProgress.visibility = View.VISIBLE
                } else {
                    titleLayout.isEnabled = true
                    descriptionLayout.isEnabled = true
                    colorChipGroup.isEnabled = true
                    applyButton.isEnabled = true
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
        return "#D32F2F"
    }

    companion object {
        fun newInstance() = AddTodoBottomSheetFragment()
    }
}