package com.example.shiftr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shiftr.data.SingleLiveEvent

class VerifyOtpViewModel : ViewModel() {

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