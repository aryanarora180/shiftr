package com.example.shiftr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shiftr.data.SingleLiveEvent
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val _application = application

    val googleSignInOptions: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("145123241245-qeld4bjknovdn7b4ppag2m5so0t6e9lm.apps.googleusercontent.com")
            .requestEmail()
            .build()

    var phoneNumber = ""

    private val _phoneNumberError = MutableLiveData<SingleLiveEvent<String>>()
    val phoneNumberError: LiveData<SingleLiveEvent<String>>
        get() = _phoneNumberError

    private val _phoneNumberValidated = MutableLiveData<SingleLiveEvent<Boolean>>()
    val phoneNumberValidated: LiveData<SingleLiveEvent<Boolean>>
        get() = _phoneNumberValidated

    // TODO: Use regex and better logic for phone number verification
    fun validateEnteredPhoneNumber(phone: String): Boolean {
        phoneNumber = if (phone.length == 10) "+91$phone" else ""
        return if (phoneNumber.isEmpty()) {
            _phoneNumberError.value = SingleLiveEvent("Invalid phone number")
            false
        } else {
            _phoneNumberValidated.value = SingleLiveEvent(true)
            true
        }
    }

    lateinit var verificationId: String
}