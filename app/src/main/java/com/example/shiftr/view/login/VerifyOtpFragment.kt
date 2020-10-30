package com.example.shiftr.view.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.shiftr.MainActivity
import com.example.shiftr.R
import com.example.shiftr.databinding.VerifyOtpFragmentBinding
import com.example.shiftr.view.showSnackbar
import com.example.shiftr.viewmodel.SignInViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class VerifyOtpFragment : Fragment() {

    private lateinit var binding: VerifyOtpFragmentBinding
    private val viewModel by activityViewModels<SignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = VerifyOtpFragmentBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.validateOtpSubHeader.text = "An OTP has been sent to ${viewModel.phoneNumber}"

        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(viewModel.phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(otpVerificationCallback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        binding.validateButton.setOnClickListener {
            val credential = PhoneAuthProvider.getCredential(viewModel.verificationId, binding.loginOtpEdit.text.toString())
            signInWithPhoneAuthCredential(credential)
        }
    }

    private val otpVerificationCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            e.printStackTrace()
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> requireView().showSnackbar("Invalid phone number")
                is FirebaseTooManyRequestsException -> requireView().showSnackbar("The SMS quota for this project has exceeded")
                else -> requireView().showSnackbar("Phone number verification failed. Please try again")
            }
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            viewModel.verificationId = verificationId
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        binding.validateOtpConstraint.visibility = View.GONE
        binding.validateOtpProgress.visibility = View.VISIBLE

        Firebase.auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // TODO: Send token and phone number to backend server for processing
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                task.exception?.printStackTrace()
                requireView().showSnackbar("Phone number verification failed. ${task.exception?.message}")
            }
        }
    }
}