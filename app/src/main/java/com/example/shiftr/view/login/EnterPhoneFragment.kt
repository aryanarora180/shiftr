package com.example.shiftr.view.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.shiftr.R
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.databinding.EnterPhoneFragmentBinding
import com.example.shiftr.view.showSnackbar
import com.example.shiftr.viewmodel.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterPhoneFragment : Fragment() {

    private lateinit var binding: EnterPhoneFragmentBinding
    private val viewModel by activityViewModels<SignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EnterPhoneFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.phoneNumberError.observe(viewLifecycleOwner, phoneNumberErrorObserver)
        viewModel.phoneNumberValidated.observe(viewLifecycleOwner, phoneNumberValidatedObserver)

        binding.loginButton.setOnClickListener {
            viewModel.validateEnteredPhoneNumber(binding.loginPhoneEdit.text.toString())
        }
    }

    private val phoneNumberErrorObserver = Observer { error: SingleLiveEvent<String> ->
        error.getContentIfNotHandled()?.let {
            requireView().showSnackbar(it)
        }
    }

    private val phoneNumberValidatedObserver = Observer { error: SingleLiveEvent<Boolean> ->
        error.getContentIfNotHandled()?.let {
            findNavController().navigate(R.id.action_enterPhoneFragment_to_verifyOtpFragment)
        }
    }
}