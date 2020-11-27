package com.example.shiftr.view.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.shiftr.R
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.databinding.AddInventoryItemBinding
import com.example.shiftr.view.showSnackbar
import com.example.shiftr.viewmodel.InventoryViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddInventoryItemBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: AddInventoryItemBinding
    private val viewModel by activityViewModels<InventoryViewModel>()

    private val units = listOf("None", "g", "Kg", "mL", "L")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddInventoryItemBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val unitDropdownAdapter = ArrayAdapter(requireContext(), R.layout.text_menu_item, units)
            unitDropdown.setAdapter(unitDropdownAdapter)

            addButton.setOnClickListener {
                viewModel.addInventoryItem(
                    nameEdit.text.toString(),
                    categoryEdit.text.toString(),
                    quantityEdit.text.toString().toFloat(),
                    unitDropdown.text.toString()
                )
            }
        }

        with(viewModel) {
            onMessageError.observe(viewLifecycleOwner, onAddTodoErrorMessageObserver)
            isAddingInventory.observe(viewLifecycleOwner, isAddingTodoObserver)
            onAddSuccess.observe(viewLifecycleOwner, onAddSuccessObserver)
        }
    }

    private val isAddingTodoObserver = Observer<SingleLiveEvent<Boolean>> {
        it.getContentIfNotHandled()?.let { isAdding ->
            with(binding) {
                if (isAdding) {
                    nameLayout.isEnabled = false
                    categoryLayout.isEnabled = false
                    quantityLayout.isEnabled = false
                    unitLayout.isEnabled = false
                    addButton.isEnabled = false
                    addButton.text = ""
                    addProgress.visibility = View.VISIBLE
                } else {
                    nameLayout.isEnabled = true
                    categoryLayout.isEnabled = true
                    quantityLayout.isEnabled = true
                    unitLayout.isEnabled = true
                    addButton.isEnabled = true
                    addButton.text = getString(R.string.add)
                    addProgress.visibility = View.GONE
                }
            }
        }
    }

    private val onAddTodoErrorMessageObserver = Observer<SingleLiveEvent<String>> {
        requireView().showSnackbar(it)
    }

    private val onAddSuccessObserver = Observer<SingleLiveEvent<Boolean>> {
        it.getContentIfNotHandled()?.let { isSuccess ->
            if (isSuccess) {
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance() = AddInventoryItemBottomSheetFragment()
    }
}