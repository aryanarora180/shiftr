package com.example.shiftr.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.databinding.EmailRegisterFragmentBinding
import com.example.shiftr.view.showSnackbar
import com.example.shiftr.viewmodel.EmailSignInViewModel
import com.example.shiftr.viewmodel.VerifyOtpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: EmailRegisterFragmentBinding
    private val verifyOtpViewModel by activityViewModels<VerifyOtpViewModel>()
    private val emailSignInViewModel by activityViewModels<EmailSignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EmailRegisterFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.setOnClickListener {
            if (verifyOtpViewModel.validateEnteredDetails(binding.phoneEdit.text.toString(), null)) {
                if (emailSignInViewModel.validatePasswords(
                        binding.emailEdit.text.toString(),
                        binding.usernameEdit.text.toString(),
                        binding.phoneEdit.text.toString(),
                        binding.professionEdit.text.toString(),
                        binding.passwordEdit.text.toString(),
                        binding.confirmPasswordEdit.text.toString(),
                    )
                ) {
                    val action =
                        RegisterFragmentDirections.actionRegisterFragmentToVerifyOtpFragment(
                            VerifyOtpFragment.FROM_REGISTRATION
                        )
                    findNavController().navigate(action)
                }
            }
        }

        emailSignInViewModel.emailSignUpError.observe(viewLifecycleOwner, emailSignUpErrorObserver)
        verifyOtpViewModel.phoneNumberError.observe(viewLifecycleOwner, emailSignUpErrorObserver)
    }

    private val emailSignUpErrorObserver = Observer<SingleLiveEvent<String>> {
        requireView().showSnackbar(it)
    }
}