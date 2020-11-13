package com.example.shiftr.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shiftr.data.OperationResult
import com.example.shiftr.data.RegisterBody
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.model.AppDataSource
import com.example.shiftr.model.DataStoreUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmailSignInViewModel @ViewModelInject constructor(
    private val repository: AppDataSource,
    private val dataStoreUtils: DataStoreUtils,
) : ViewModel() {

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
                    with(result.data.tokens) {
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

    private val _emailSignUpError = MutableLiveData<SingleLiveEvent<String>>()
    val emailSignUpError: LiveData<SingleLiveEvent<String>>
        get() = _emailSignUpError

    lateinit var user: RegisterBody

    fun validatePasswords(
        email: String,
        username: String,
        phoneNumber: String,
        profession: String,
        password: String,
        confirmPassword: String,
    ): Boolean {
        return if (password == confirmPassword) {
            user = RegisterBody(
                email, username, password, phoneNumber, profession
            )
            true
        } else {
            _emailSignUpError.postValue(SingleLiveEvent("Passwords must match"))
            false
        }
    }

    private val _emailSignUpSuccess = MutableLiveData<SingleLiveEvent<Boolean>>()
    val emailSignUpSuccess: LiveData<SingleLiveEvent<Boolean>>
        get() = _emailSignUpSuccess

    fun signUpWithEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.registerUserWithEmail(user)) {
                is OperationResult.Success -> {
                    _emailSignUpSuccess.postValue(SingleLiveEvent(true))
                }
                is OperationResult.Error -> {
                    _emailSignUpError.postValue(SingleLiveEvent(result.message))
                }
            }
        }
    }
}