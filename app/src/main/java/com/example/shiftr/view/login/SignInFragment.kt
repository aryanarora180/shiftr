package com.example.shiftr.view.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.shiftr.MainActivity
import com.example.shiftr.R
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.databinding.SignInFragmentBinding
import com.example.shiftr.view.showSnackbar
import com.example.shiftr.viewmodel.SignInViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignInFragment : Fragment() {

    private lateinit var binding: SignInFragmentBinding
    private val viewModel by activityViewModels<SignInViewModel>()

    private lateinit var _googleSignInClient: GoogleSignInClient
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            } else {
                requireView().showSnackbar("Google sign in failed. Please try again later")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SignInFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _googleSignInClient = GoogleSignIn.getClient(
            requireContext(),
            viewModel.googleSignInOptions
        )

        binding.submitButton.setOnClickListener {
            hideKeyboard()
            viewModel.signInWithEmail(
                binding.emailEdit.text.toString(),
                binding.passwordEdit.text.toString()
            )
        }

        binding.googleSignInButton.setOnClickListener {
            startForResult.launch(_googleSignInClient.signInIntent)
        }

        viewModel.emailIsSigningIn.observe(viewLifecycleOwner, emailIsSigningInObserver)
        viewModel.emailSignInError.observe(viewLifecycleOwner, emailSignInErrorObserver)
        viewModel.emailSignInSuccess.observe(viewLifecycleOwner, emailSignInSuccessObserver)
    }

    private val emailIsSigningInObserver = Observer<Boolean> {
        with(binding) {
            if (it) {
                submitButton.text = ""
                signInProgress.visibility = View.VISIBLE
                googleSignInButton.isEnabled = false
                signUpText.isEnabled = false
            } else {
                submitButton.text = getString(R.string.submit)
                signInProgress.visibility = View.GONE
                googleSignInButton.isEnabled = true
                signUpText.isEnabled = true
            }
        }
    }

    private val emailSignInErrorObserver = Observer<SingleLiveEvent<String>> {
        requireView().showSnackbar(it)
    }

    private val emailSignInSuccessObserver = Observer<SingleLiveEvent<Boolean>> {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        if (completedTask.isSuccessful) {
            findNavController().navigate(R.id.action_googleSignInFragment_to_enterPhoneFragment)
        } else {
            requireView().showSnackbar("Google sign in failed. Please try again later")
        }
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}