package com.example.shiftr.view.inventory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.shiftr.R
import com.example.shiftr.data.InventoryItem
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.databinding.InventoryFragmentBinding
import com.example.shiftr.view.SpringyRecycler
import com.example.shiftr.view.adapter.InventoryAdapter
import com.example.shiftr.view.showSnackbar
import com.example.shiftr.viewmodel.InventoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryFragment : Fragment() {

    private lateinit var binding: InventoryFragmentBinding
    private val viewModel by viewModels<InventoryViewModel>()

    private val inventoryAdapter = InventoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = InventoryFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black)

        inventoryAdapter.increaseQuantityListener = {
            viewModel.updateInventoryItemQuantity(it, true)
        }
        inventoryAdapter.decreaseQuantityListener = {
            viewModel.updateInventoryItemQuantity(it, false)
        }
        inventoryAdapter.deleteListener = {
            showDeleteItemConfirmation(it)
        }

        binding.inventoryRecycler.apply {
            edgeEffectFactory =
                SpringyRecycler.springEdgeEffectFactory<InventoryAdapter.InventoryViewHolder>()
            adapter = inventoryAdapter
        }
        binding.addInventoryFab.setOnClickListener {
            AddInventoryItemBottomSheetFragment(viewModel).show(childFragmentManager, "add-inventory-item")
        }

        with(viewModel) {
            inventory.observe(viewLifecycleOwner, inventoryObserver)
            isEmptyList.observe(viewLifecycleOwner, isEmptyListObserver)
            onMessageError.observe(viewLifecycleOwner, onErrorObserver)
            isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
        }
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        with(binding) {
            if (isLoading) {
                inventoryProgress.visibility = View.VISIBLE
                inventoryRecycler.visibility = View.GONE
                noInventoryImage.visibility = View.GONE
                noInventoryText.visibility = View.GONE
            } else {
                inventoryProgress.visibility = View.GONE
            }
        }
    }

    private val isEmptyListObserver = Observer<Boolean> { isEmpty ->
        with(binding) {
            if (isEmpty) {
                inventorySearchLayout.visibility = View.GONE
                inventoryRecycler.visibility = View.INVISIBLE
                noInventoryImage.visibility = View.VISIBLE
                noInventoryText.visibility = View.VISIBLE
            } else {
                noInventoryImage.visibility = View.GONE
                noInventoryText.visibility = View.GONE
            }
        }
    }

    private val inventoryObserver = Observer<List<InventoryItem>> { inventoryItems ->
        with(binding) {
            inventorySearchLayout.visibility = View.VISIBLE
            inventoryRecycler.visibility = View.VISIBLE
            inventoryAdapter.data = inventoryItems

            inventorySearchEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Do nothing
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        inventoryAdapter.data = inventoryItems
                    } else {
                        inventoryAdapter.data = inventoryItems.filter { it.name.contains(s, ignoreCase = true) or it.category.contains(s, ignoreCase = true)}
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    // Do nothing
                }
            })
        }
    }

    private val onErrorObserver = Observer<SingleLiveEvent<String>> { error ->
        requireView().showSnackbar(error)
    }

    private fun showDeleteItemConfirmation(inventoryItem: InventoryItem) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Delete inventory item")
            setMessage("Are you sure you want to delete ${inventoryItem.name} from your inventory?")
            setPositiveButton("Yes") { _, _ ->
                viewModel.deleteInventoryItem(inventoryItem)
            }
            setNeutralButton("Cancel") { _, _ -> /* Do nothing */ }
            show()
        }
    }
}