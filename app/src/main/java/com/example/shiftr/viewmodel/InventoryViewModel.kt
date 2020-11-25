package com.example.shiftr.viewmodel

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shiftr.data.InventoryItem
import com.example.shiftr.data.OperationResult
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.model.AppDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("NullSafeMutableLiveData")
class InventoryViewModel @ViewModelInject constructor(
    private val repository: AppDataSource
) : ViewModel() {

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean>
        get() = _isDataLoading

    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean>
        get() = _isEmptyList

    private val _onErrorMessage = MutableLiveData<SingleLiveEvent<String>>()
    val onMessageError: LiveData<SingleLiveEvent<String>>
        get() = _onErrorMessage

    private val _inventory = MutableLiveData<List<InventoryItem>>()
    val inventory: LiveData<List<InventoryItem>>
        get() = _inventory

    fun loadInventory() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.getInventory()) {
                is OperationResult.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        _isEmptyList.postValue(true)
                    } else {
                        _isEmptyList.postValue(false)
                        _inventory.postValue(result.data)
                    }
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

    private val _isAddingInventory = MutableLiveData<SingleLiveEvent<Boolean>>()
    val isAddingInventory: LiveData<SingleLiveEvent<Boolean>>
        get() = _isAddingInventory

    private val _onAddSuccess = MutableLiveData<SingleLiveEvent<Boolean>>()
    val onAddSuccess: LiveData<SingleLiveEvent<Boolean>>
        get() = _onAddSuccess

    fun addInventoryItem(
        name: String,
        category: String,
        quantity: Float,
        unit: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isAddingInventory.postValue(SingleLiveEvent(true))
            when (val result = repository.addInventoryItem(name, category, quantity, unit)) {
                is OperationResult.Success -> {
                    loadInventory()
                    _onAddSuccess.postValue(SingleLiveEvent(true))
                }
                is OperationResult.Error -> {
                    _onErrorMessage.postValue(
                        SingleLiveEvent(
                            result.message
                        )
                    )
                }
            }
            _isAddingInventory.postValue(SingleLiveEvent(false))
        }
    }

    init {
        loadInventory()
    }
}