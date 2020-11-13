package com.example.shiftr.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.shiftr.R
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.databinding.EmailRegisterFragmentBinding
import com.example.shiftr.view.showSnackbar
import com.example.shiftr.viewmodel.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: EmailRegisterFragmentBinding
    private val viewModel by activityViewModels<SignInViewModel>()

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
            viewModel.signUpWithEmail(
                binding.emailEdit.text.toString(),
                binding.usernameEdit.text.toString(),
                binding.phoneEdit.text.toString(),
                binding.professionEdit.text.toString(),
                binding.passwordEdit.text.toString(),
                binding.confirmPasswordEdit.text.toString(),
            )
        }

        with(viewModel) {
            emailIsSigningUp.observe(viewLifecycleOwner, emailIsSigningUpObserver)
            emailSignUpError.observe(viewLifecycleOwner, emailSignUpErrorObserver)
            emailSignUpSuccess.observe(viewLifecycleOwner, emailSignUpSuccessObserver)
        }
    }

    private val emailIsSigningUpObserver = Observer<Boolean> {
        with(binding) {
            if (it) {
                submitButton.text = ""
                signUpProgress.visibility = View.VISIBLE

                emailLayout.isEnabled = false
                usernameLayout.isEnabled = false
                phoneLayout.isEnabled = false
                professionLayout.isEnabled = false
                passwordLayout.isEnabled = false
                confirmPasswordLayout.isEnabled = false
            } else {
                submitButton.text = getString(R.string.submit)
                signUpProgress.visibility = View.GONE

                emailLayout.isEnabled = true
                usernameLayout.isEnabled = true
                phoneLayout.isEnabled = true
                professionLayout.isEnabled = true
                passwordLayout.isEnabled = true
                confirmPasswordLayout.isEnabled = true
            }
        }
    }

    private val emailSignUpErrorObserver = Observer<SingleLiveEvent<String>> {
        requireView().showSnackbar(it)
    }

    private val emailSignUpSuccessObserver = Observer<SingleLiveEvent<Boolean>> {
        with(binding) {
            emailEdit.setText("")
            usernameEdit.setText("")
            phoneEdit.setText("")
            professionEdit.setText("")
            passwordEdit.setText("")
            confirmPasswordEdit.setText("")
        }

        requireView().showSnackbar("Please check your email for a verification link.")
    }
}