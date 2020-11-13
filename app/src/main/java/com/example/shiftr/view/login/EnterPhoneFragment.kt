package com.example.shiftr.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.shiftr.R
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.databinding.EnterPhoneFragmentBinding
import com.example.shiftr.view.showSnackbar
import com.example.shiftr.viewmodel.VerifyOtpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterPhoneFragment : Fragment() {

    private lateinit var binding: EnterPhoneFragmentBinding
    private val viewModel by activityViewModels<VerifyOtpViewModel>()

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
            viewModel.validateEnteredDetails(binding.loginPhoneEdit.text.toString(), binding.professionEdit.text.toString())
        }
    }

    private val phoneNumberErrorObserver = Observer { error: SingleLiveEvent<String> ->
        error.getContentIfNotHandled()?.let {
            requireView().showSnackbar(it)
        }
    }

    private val phoneNumberValidatedObserver = Observer { error: SingleLiveEvent<Boolean> ->
        error.getContentIfNotHandled()?.let {
            val action = EnterPhoneFragmentDirections.actionEnterPhoneFragmentToVerifyOtpFragment(VerifyOtpFragment.FROM_GOOGLE)
            findNavController().navigate(action)
        }
    }
}