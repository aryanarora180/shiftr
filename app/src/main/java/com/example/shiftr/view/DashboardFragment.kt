package com.example.shiftr.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.shiftr.LoginActivity
import com.example.shiftr.R
import com.example.shiftr.data.DashboardResponse
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.databinding.DashboardFragmentBinding
import com.example.shiftr.viewmodel.DashboardViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black)

        with(binding) {
            signOutImage.setOnClickListener { showSignOutConfirmationDialog() }

            completedTodosCard.setOnClickListener { findNavController().navigate(R.id.toDoListFragment) }
            pendingTodosCard.setOnClickListener { findNavController().navigate(R.id.toDoListFragment) }
            inventoryItemsCard.setOnClickListener { findNavController().navigate(R.id.inventoryFragment) }

            shareImage.setOnClickListener { takeScreeShot() }
        }

        with(viewModel) {
            isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
            onMessageError.observe(viewLifecycleOwner, onErrorObserver)
            dashboard.observe(viewLifecycleOwner, dashboardObserver)
            onDashboardSave.observe(viewLifecycleOwner, openImageObserver)
        }
    }

    private fun takeScreeShot() {
        val bitmap = Bitmap.createBitmap(
            requireView().width,
            requireView().height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        requireView().draw(canvas)

        viewModel.saveDashboardScreenshot(bitmap)
    }

    private val openImageObserver = Observer<SingleLiveEvent<Uri>> { event ->
        event.getContentIfNotHandled()?.let {
            val intent = Intent(Intent.ACTION_SEND)
            intent.apply {
                putExtra(Intent.EXTRA_STREAM, it)
                putExtra(Intent.EXTRA_TEXT, "This is my dashboard on the shiftr app!")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/png"
            }
            startActivity(intent)
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

    private fun showSignOutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Sign out")
            setMessage("Are you sure you want to sign out?")
            setPositiveButton("Yes") { _, _ ->
                viewModel.signOut()
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finish()
            }
            setNeutralButton("Cancel") { _, _ -> /* Do nothing */ }
            show()
        }
    }
}