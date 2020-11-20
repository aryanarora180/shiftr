package com.example.shiftr.viewmodel

import android.annotation.SuppressLint
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.shiftr.data.OperationResult
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.data.Todo
import com.example.shiftr.data.TodoItem
import com.example.shiftr.model.AppDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("NullSafeMutableLiveData")
class ViewTodoViewModel @ViewModelInject constructor(
    private val repository: AppDataSource,
    @Assisted private val savedStateHandle: SavedStateHandle
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

    private val _todoItems = MutableLiveData<List<TodoItem>>()
    val todoItems: LiveData<List<TodoItem>>
        get() = _todoItems

    private fun loadTodoItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = savedStateHandle.get<Todo>("todo")?.id
            if (id != null) {
                _isDataLoading.postValue(true)
                when (val result = repository.getTodoItems(id)) {
                    is OperationResult.Success -> {
                        if (result.data.isNullOrEmpty()) {
                            _isEmptyList.postValue(true)
                        } else {
                            _isEmptyList.postValue(false)
                            _todoItems.postValue(result.data)
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
            } else {
                _onDeleteTodoErrorMessage.postValue(
                    SingleLiveEvent(
                        "Something went wrong. Please try again later"
                    )
                )
            }
        }
    }

    init {
        loadTodoItems()
    }

    private val _isDeletingTodo = MutableLiveData<Boolean>()
    val isDeletingTodo: LiveData<Boolean>
        get() = _isDeletingTodo

    private val _onDeleteTodoErrorMessage = MutableLiveData<SingleLiveEvent<String>>()
    val onDeleteTodoErrorMessage: LiveData<SingleLiveEvent<String>>
        get() = _onDeleteTodoErrorMessage

    private val _onDeleteTodoSuccess = MutableLiveData<SingleLiveEvent<Boolean>>()
    val onDeleteTodoSuccess: LiveData<SingleLiveEvent<Boolean>>
        get() = _onDeleteTodoSuccess

    fun deleteTodo() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = savedStateHandle.get<Todo>("todo")?.id
            if (id != null) {
                _isDeletingTodo.postValue(true)
                when (val result = repository.deleteTodo(id)) {
                    is OperationResult.Success -> {
                        _onDeleteTodoSuccess.postValue(SingleLiveEvent(true))
                    }
                    is OperationResult.Error -> {
                        _onDeleteTodoErrorMessage.postValue(
                            SingleLiveEvent(
                                result.message
                            )
                        )
                    }
                }
                _isDeletingTodo.postValue(false)
            } else {
                _onDeleteTodoErrorMessage.postValue(
                    SingleLiveEvent(
                        "Something went wrong. Please try again later"
                    )
                )
            }
        }
    }
}