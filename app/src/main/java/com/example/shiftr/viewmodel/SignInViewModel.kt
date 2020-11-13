package com.example.shiftr.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shiftr.data.OperationResult
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.model.AppDataSource
import com.example.shiftr.model.DataStoreUtils
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignInViewModel @ViewModelInject constructor(
    private val repository: AppDataSource,
    private val dataStoreUtils: DataStoreUtils,
    application: Application
) : AndroidViewModel(application) {

    private val _application = application

    private val _emailIsSigningIn = MutableLiveData<Boolean>().apply { value = false }
    val emailIsSigningIn: LiveData<Boolean>
        get() = _emailIsSigningIn

    private val _emailSignInError = MutableLiveData<SingleLiveEvent<String>>()
    val emailSignInError: LiveData<SingleLiveEvent<String>>
        get() = _emailSignInError

    private val _emailSignInSuccess = MutableLiveData<SingleLiveEvent<Boolean>>()
    val emailSignInSuccess: LiveData<SingleLiveEvent<Boolean>>
        get() = _emailSignInSuccess

    fun signInWithEmail(emailId: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _emailIsSigningIn.postValue(true)
            when (val result = repository.loginUserWithEmail(emailId, password)) {
                is OperationResult.Success -> {
                    with(result.data.data.tokens) {
                        dataStoreUtils.storeAccessToken(accessToken)
                        dataStoreUtils.storeRefreshToken(refreshToken)
                    }
                    _emailSignInSuccess.postValue(SingleLiveEvent(true))
                }
                is OperationResult.Error -> {
                    _emailSignInError.postValue(SingleLiveEvent(result.message))
                }
            }
            _emailIsSigningIn.postValue(false)
        }
    }

    private val _emailIsSigningUp = MutableLiveData<Boolean>().apply { value = false }
    val emailIsSigningUp: LiveData<Boolean>
        get() = _emailIsSigningUp

    private val _emailSignUpError = MutableLiveData<SingleLiveEvent<String>>()
    val emailSignUpError: LiveData<SingleLiveEvent<String>>
        get() = _emailSignUpError

    private val _emailSignUpSuccess = MutableLiveData<SingleLiveEvent<Boolean>>()
    val emailSignUpSuccess: LiveData<SingleLiveEvent<Boolean>>
        get() = _emailSignUpSuccess

    fun signUpWithEmail(
        email: String,
        username: String,
        phoneNumber: String,
        profession: String,
        password: String,
        confirmPassword: String,
        ) {
        viewModelScope.launch(Dispatchers.IO) {
            _emailIsSigningUp.postValue(true)
            if (password == confirmPassword) {
                when (val result = repository.registerUserWithEmail(email, username, password, phoneNumber, profession)) {
                    is OperationResult.Success -> {
                        _emailSignUpSuccess.postValue(SingleLiveEvent(true))
                    }
                    is OperationResult.Error -> {
                        _emailSignUpError.postValue(SingleLiveEvent(result.message))
                    }
                }
            } else {
                _emailSignUpError.postValue(SingleLiveEvent("Passwords must match"))
            }
            _emailIsSigningUp.postValue(false)
        }
    }

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