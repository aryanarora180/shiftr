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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

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
                _onErrorMessage.postValue(
                    SingleLiveEvent(
                        "Something went wrong. Please try again later"
                    )
                )
            }
        }
    }

    fun toggleTodoDone(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = savedStateHandle.get<Todo>("todo")?.id
            if (id != null) {
                _isDataLoading.postValue(true)
                when (val result =
                    repository.updateTodoItemDone(todoItem.itemId, id, !todoItem.done)) {
                    is OperationResult.Success -> {
                        loadTodoItems()
                    }
                    is OperationResult.Error -> {
                        _onErrorMessage.postValue(
                            SingleLiveEvent(
                                result.message
                            )
                        )
                        _isDataLoading.postValue(false)
                    }
                }
            } else {
                _onErrorMessage.postValue(
                    SingleLiveEvent(
                        "Something went wrong. Please try again later"
                    )
                )
            }
        }
    }

    fun deleteTodoItem(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.deleteTodoItem(todoItem.itemId)) {
                is OperationResult.Success -> {
                    loadTodoItems()
                }
                is OperationResult.Error -> {
                    _onErrorMessage.postValue(
                        SingleLiveEvent(
                            result.message
                        )
                    )
                    _isDataLoading.postValue(false)
                }
            }
        }
    }

    fun getShareText(): String {
        val title = savedStateHandle.get<Todo>("todo")?.title

        val shareText = StringBuilder("This is my todo list for $title on shiftr:\n\n")
        _todoItems.value?.forEach {
            shareText.append("${it.itemText} (${it.getPriorityText()}) ${formatDate(it.deadline)} - ${if (it.done) "Completed" else "Pending"} \n")
        }
        return shareText.toString()
    }

    private val shareDateFormatter = DateTimeFormatter.ofPattern("E, MMM dd", Locale.ENGLISH)
    private val shareTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
    private fun formatDate(date: String): String {
        val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return "due ${parsedDate.format(shareDateFormatter)} at ${parsedDate.format(shareTimeFormatter)}"
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

    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
    fun formatDate(dateEpoch: Long): String {
        val parsedDate =
            Instant.ofEpochMilli(dateEpoch).atZone(ZoneId.systemDefault()).toLocalDate()
        return parsedDate.format(dateFormatter)
    }

    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
    fun formatTime(hours: Int, minutes: Int): String {
        val parsedTime = DateTimeFormatter.ISO_TIME.parse(getTimeText(hours, minutes))
        return timeFormatter.format(parsedTime)
    }

    private fun getTimeText(hours: Int, minutes: Int): String {
        var time = ""
        time += if (hours in 0..9) "0$hours" else hours
        time += if (minutes in 0..9) ":0$minutes" else ":$minutes"
        return time
    }

    private fun formatDateForApi(dateAndTime: String): String {
        val parsedTime =
            LocalDateTime.parse(dateAndTime, DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"))
                .atZone(
                    ZoneId.systemDefault()
                )
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault())
            .format(parsedTime)
    }

    private val _isAddingTodoItem = MutableLiveData<SingleLiveEvent<Boolean>>()
    val isAddingTodoItem: LiveData<SingleLiveEvent<Boolean>>
        get() = _isAddingTodoItem

    private val _onAddTodoItemErrorMessage = MutableLiveData<SingleLiveEvent<String>>()
    val onAddTodoItemErrorMessage: LiveData<SingleLiveEvent<String>>
        get() = _onAddTodoItemErrorMessage

    private val _onAddTodoItemSuccess = MutableLiveData<SingleLiveEvent<Boolean>>()
    val onAddTodoItemSuccess: LiveData<SingleLiveEvent<Boolean>>
        get() = _onAddTodoItemSuccess

    fun addTodoItem(
        itemText: String,
        priority: String,
        deadlineDate: String,
        deadlineTime: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = savedStateHandle.get<Todo>("todo")?.id
            if (id != null) {
                _isAddingTodoItem.postValue(SingleLiveEvent(true))
                when (val result = repository.addTodoItem(
                    itemText,
                    id,
                    false,
                    priority,
                    formatDateForApi("$deadlineDate $deadlineTime")
                )) {
                    is OperationResult.Success -> {
                        loadTodoItems()
                        _onAddTodoItemSuccess.postValue(SingleLiveEvent(true))
                    }
                    is OperationResult.Error -> {
                        _onAddTodoItemErrorMessage.postValue(
                            SingleLiveEvent(
                                result.message
                            )
                        )
                    }
                }
                _isAddingTodoItem.postValue(SingleLiveEvent(false))
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