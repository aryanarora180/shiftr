package com.example.shiftr.view.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.shiftr.MainActivity
import com.example.shiftr.R
import com.example.shiftr.databinding.GoogleSignInFragmentBinding
import com.example.shiftr.view.showSnackbar
import com.example.shiftr.viewmodel.SignInViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoogleSignInFragment : Fragment() {

    private lateinit var binding: GoogleSignInFragmentBinding
    private val viewModel by activityViewModels<SignInViewModel>()

    private lateinit var _googleSignInClient: GoogleSignInClient
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
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
        binding = GoogleSignInFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _googleSignInClient = GoogleSignIn.getClient(
            requireContext(),
            viewModel.googleSignInOptions
        )

        binding.googleSignInButton.setOnClickListener {
            startForResult.launch(_googleSignInClient.signInIntent)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        if (completedTask.isSuccessful) {
            findNavController().navigate(R.id.action_googleSignInFragment_to_enterPhoneFragment)
        } else {
            requireView().showSnackbar("Google sign in failed. Please try again later")
        }
    }
}