package com.example.shiftr.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.shiftr.R
import com.example.shiftr.data.DashboardResponse
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.databinding.DashboardFragmentBinding
import com.example.shiftr.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private lateinit var binding: DashboardFragmentBinding
    private val viewModel by viewModels<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DashboardFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            completedTodosCard.setOnClickListener { findNavController().navigate(R.id.toDoListFragment) }
            pendingTodosCard.setOnClickListener { findNavController().navigate(R.id.toDoListFragment) }
            inventoryItemsCard.setOnClickListener { findNavController().navigate(R.id.inventoryFragment) }
        }

        with(viewModel) {
            isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
            onMessageError.observe(viewLifecycleOwner, onErrorObserver)
            dashboard.observe(viewLifecycleOwner, dashboardObserver)
        }
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        with(binding) {
            if (isLoading) {
                dashboardConstraint.visibility = View.GONE
                dashboardProgress.visibility = View.VISIBLE
            } else {
                dashboardProgress.visibility = View.GONE
            }
        }
    }

    private val onErrorObserver = Observer<SingleLiveEvent<String>> { error ->
        binding.dashboardConstraint.visibility = View.GONE
        requireView().showSnackbar(error)
    }

    private val dashboardObserver = Observer<DashboardResponse> {
        with(binding) {
            dashboardConstraint.visibility = View.VISIBLE
            usernameText.text = it.username
            professionText.text = it.profession
            completedTodosText.text = it.completedTdo.toString()
            inventoryItemsText.text = it.inventory.toString()
            pendingTodosText.text = it.pendingTodo.toString()
        }
    }
}