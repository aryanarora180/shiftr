package com.example.shiftr.view.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shiftr.LoginActivity
import com.example.shiftr.MainActivity
import com.example.shiftr.R
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.databinding.VerifyOtpFragmentBinding
import com.example.shiftr.view.showSnackbar
import com.example.shiftr.viewmodel.EmailSignInViewModel
import com.example.shiftr.viewmodel.VerifyOtpViewModel
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
    private val args by navArgs<VerifyOtpFragmentArgs>()
    private val emailSignInViewModel by activityViewModels<EmailSignInViewModel>()
    private val verifyOtpViewModel by activityViewModels<VerifyOtpViewModel>()

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

        binding.validateOtpSubHeader.text = "An OTP has been sent to ${verifyOtpViewModel.phoneNumber}"

        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber("+91 ${verifyOtpViewModel.phoneNumber}")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(otpVerificationCallback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        binding.validateButton.setOnClickListener {
            val credential = PhoneAuthProvider.getCredential(verifyOtpViewModel.verificationId, binding.loginOtpEdit.text.toString())
            signInWithPhoneAuthCredential(credential)
        }

        emailSignInViewModel.emailSignUpSuccess.observe(viewLifecycleOwner, emailSignUpSuccessObserver)
        with(verifyOtpViewModel) {
            googleSignInError.observe(viewLifecycleOwner, googleSignInErrorObserver)
            googleSignInSuccess.observe(viewLifecycleOwner, googleSignInSuccessObserver)
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
            verifyOtpViewModel.verificationId = verificationId
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        binding.validateOtpConstraint.visibility = View.GONE
        binding.validateOtpProgress.visibility = View.VISIBLE

        Firebase.auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (args.from == FROM_GOOGLE) {
                    verifyOtpViewModel.signUpWithGoogle()
                } else {
                    emailSignInViewModel.signUpWithEmail()
                }
            } else {
                requireView().showSnackbar("Phone number verification failed. ${task.exception?.message}")
            }
        }
    }

    private val emailSignUpSuccessObserver = Observer<SingleLiveEvent<Boolean>> {
        it.getContentIfNotHandled()?.let { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireActivity(), "Please check your email for a verification link", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_verifyOtpFragment_to_googleSignInFragment)
            }
        }
    }

    private val googleSignInSuccessObserver = Observer<SingleLiveEvent<Boolean>> {
        it.getContentIfNotHandled()?.let { isSuccess ->
            if (isSuccess) {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    private val googleSignInErrorObserver = Observer<SingleLiveEvent<String>> {
        requireView().showSnackbar(it)
    }

    companion object {
        const val FROM_GOOGLE = 100
        const val FROM_REGISTRATION = 101
    }
}