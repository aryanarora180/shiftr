package com.example.shiftr.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.shiftr.data.InventoryItem
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.databinding.InventoryFragmentBinding
import com.example.shiftr.view.adapter.InventoryAdapter
import com.example.shiftr.view.todo.AddTodoBottomSheetFragment
import com.example.shiftr.viewmodel.InventoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryFragment : Fragment() {

    private lateinit var binding: InventoryFragmentBinding
    private val viewModel by viewModels<InventoryViewModel>()

    private val inventoryAdapter = InventoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = InventoryFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inventoryAdapter.deleteListener = { itemToDelete ->
            // TODO
        }
        binding.inventoryRecycler.apply {
            edgeEffectFactory =
                SpringyRecycler.springEdgeEffectFactory<InventoryAdapter.InventoryViewHolder>()
            adapter = inventoryAdapter
        }
        binding.addInventoryFab.setOnClickListener {
            AddTodoBottomSheetFragment.newInstance().show(childFragmentManager, "add-inventory-item")
        }

        with(viewModel) {
            inventory.observe(viewLifecycleOwner, todoObserver)
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
                inventoryRecycler.visibility = View.INVISIBLE
                noInventoryImage.visibility = View.VISIBLE
                noInventoryText.visibility = View.VISIBLE
            } else {
                noInventoryImage.visibility = View.GONE
                noInventoryText.visibility = View.GONE
            }
        }
    }

    private val todoObserver = Observer<List<InventoryItem>> { inventoryItems ->
        with(binding) {
            inventoryRecycler.visibility = View.VISIBLE
            inventoryAdapter.data = inventoryItems
        }
    }

    private val onErrorObserver = Observer<SingleLiveEvent<String>> { error ->
        requireView().showSnackbar(error)
    }
}