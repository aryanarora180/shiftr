package com.example.shiftr.viewmodel

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.shiftr.data.OperationResult
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.model.AppDataSource
import com.example.shiftr.model.DataStoreUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VerifyOtpViewModel @ViewModelInject constructor(
    private val repository: AppDataSource,
    private val dataStoreUtils: DataStoreUtils,
    application: Application
) : AndroidViewModel(application) {

    private val _application = application

    var profession = ""
    var phoneNumber = ""

    private val _phoneNumberError = MutableLiveData<SingleLiveEvent<String>>()
    val phoneNumberError: LiveData<SingleLiveEvent<String>>
        get() = _phoneNumberError

    private val _phoneNumberValidated = MutableLiveData<SingleLiveEvent<Boolean>>()
    val phoneNumberValidated: LiveData<SingleLiveEvent<Boolean>>
        get() = _phoneNumberValidated

    // TODO: Use regex and better logic for phone number verification
    fun validateEnteredDetails(phone: String, profession: String?): Boolean {
        this.profession = profession ?: ""
        phoneNumber = if (phone.length == 10) phone else ""
        return if (phoneNumber.isEmpty()) {
            _phoneNumberError.value = SingleLiveEvent("Invalid phone number")
            false
        } else {
            _phoneNumberValidated.value = SingleLiveEvent(true)
            true
        }
    }

    lateinit var verificationId: String

    private val _googleSignInError = MutableLiveData<SingleLiveEvent<String>>()
    val googleSignInError: LiveData<SingleLiveEvent<String>>
        get() = _googleSignInError

    private val _googleSignInSuccess = MutableLiveData<SingleLiveEvent<Boolean>>()
    val googleSignInSuccess: LiveData<SingleLiveEvent<Boolean>>
        get() = _googleSignInSuccess

    fun signUpWithGoogle() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.loginUserWithGoogle(GoogleSignIn.getLastSignedInAccount(_application)?.idToken ?: "", phoneNumber, profession)) {
                is OperationResult.Success -> {
                    with(result.data.tokenData.tokens) {
                        dataStoreUtils.storeAccessToken(accessToken)
                        dataStoreUtils.storeRefreshToken(refreshToken)
                    }
                    _googleSignInSuccess.postValue(SingleLiveEvent(true))
                }
                is OperationResult.Error -> {
                    _googleSignInError.postValue(SingleLiveEvent(result.message))
                }
            }
        }
    }
}