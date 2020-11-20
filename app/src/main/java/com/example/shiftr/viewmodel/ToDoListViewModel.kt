package com.example.shiftr.viewmodel

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shiftr.data.OperationResult
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.data.Todo
import com.example.shiftr.model.AppDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("NullSafeMutableLiveData")
class ToDoListViewModel @ViewModelInject constructor(
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

    private val _todos = MutableLiveData<List<Todo>>()
    val todos: LiveData<List<Todo>>
        get() = _todos

    fun loadTodos() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.getTodo()) {
                is OperationResult.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        _isEmptyList.postValue(true)
                    } else {
                        _isEmptyList.postValue(false)
                        _todos.postValue(result.data)
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

    private val _isAddingTodo = MutableLiveData<SingleLiveEvent<Boolean>>()
    val isAddingTodo: LiveData<SingleLiveEvent<Boolean>>
        get() = _isAddingTodo

    private val _onAddTodoErrorMessage = MutableLiveData<SingleLiveEvent<String>>()
    val onAddTodoErrorMessage: LiveData<SingleLiveEvent<String>>
        get() = _onAddTodoErrorMessage

    private val _onAddTodoSuccess = MutableLiveData<SingleLiveEvent<Boolean>>()
    val onAddTodoSuccess: LiveData<SingleLiveEvent<Boolean>>
        get() = _onAddTodoSuccess

    fun addTodo(name: String, description: String, color: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isAddingTodo.postValue(SingleLiveEvent(true))
            when (val result = repository.addTodo(name, description, color)) {
                is OperationResult.Success -> {
                    loadTodos()
                    _onAddTodoSuccess.postValue(SingleLiveEvent(true))
                }
                is OperationResult.Error -> {
                    _onAddTodoErrorMessage.postValue(
                        SingleLiveEvent(
                            result.message
                        )
                    )
                }
            }
            _isAddingTodo.postValue(SingleLiveEvent(false))
        }
    }

    init {
        loadTodos()
    }
}