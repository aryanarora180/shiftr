package com.example.shiftr.viewmodel

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shiftr.data.DashboardResponse
import com.example.shiftr.data.OperationResult
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.model.AppDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("NullSafeMutableLiveData")
class DashboardViewModel @ViewModelInject constructor(
    private val repository: AppDataSource
) : ViewModel() {

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean>
        get() = _isDataLoading

    private val _onErrorMessage = MutableLiveData<SingleLiveEvent<String>>()
    val onMessageError: LiveData<SingleLiveEvent<String>>
        get() = _onErrorMessage

    private val _dashboard = MutableLiveData<DashboardResponse>()
    val dashboard: LiveData<DashboardResponse>
        get() = _dashboard

    private fun loadDashboard() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.getDashboard()) {
                is OperationResult.Success -> {
                    _dashboard.postValue(result.data)
                }
                is OperationResult.Error -> {
                    _onErrorMessage.postValue(
                        SingleLiveEvent(
                            result.message
                        )
                    )
                }
            }
            _isDataLoading.postValue(false)
        }
    }

    init {
        loadDashboard()
    }
}