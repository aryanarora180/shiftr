package com.example.shiftr.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import android.text.format.Formatter
import android.util.Log
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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@SuppressLint("NullSafeMutableLiveData")
class ViewTodoViewModel @ViewModelInject constructor(
    private val repository: AppDataSource,
    @Assisted private val savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {

    private val _application = application

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
        return "due ${parsedDate.format(shareDateFormatter)} at ${
            parsedDate.format(
                shareTimeFormatter
            )
        }"
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
                        if (selectedUri != null) {
                            uploadAttachmentForTodo(result.data.itemId)
                        } else {
                            scheduleEmailForTodoItem(result.data.itemId)
                        }
                    }
                    is OperationResult.Error -> {
                        _onAddTodoItemErrorMessage.postValue(
                            SingleLiveEvent(
                                result.message
                            )
                        )
                        _isAddingTodoItem.postValue(SingleLiveEvent(false))
                    }
                }
            } else {
                _onDeleteTodoErrorMessage.postValue(
                    SingleLiveEvent(
                        "Something went wrong. Please try again later"
                    )
                )
            }
        }
    }

    private fun scheduleEmailForTodoItem(todoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.scheduleEmailForTodoItem(todoId)) {
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
                    _isAddingTodoItem.postValue(SingleLiveEvent(false))
                }
            }
        }
    }

    private val _fileDetails = MutableLiveData<String>()
    val fileDetails: LiveData<String>
        get() = _fileDetails

    var selectedUri: Uri? = null
        set(value) {
            field = value
            if (value == null) {
                _fileDetails.postValue("")
            } else {
                var fileText = ""
                selectedUri?.let { returnUri ->
                    _application.contentResolver.query(returnUri, null, null, null, null)
                }?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    cursor.moveToFirst()
                    fileText = "${cursor.getString(nameIndex)} (${
                        Formatter.formatFileSize(
                            _application,
                            cursor.getLong(sizeIndex)
                        )
                    })"
                }
                _fileDetails.postValue(fileText)
            }
        }

    private val externalFileDir = application.getExternalFilesDir(null).toString()
    fun getFile(): MultipartBody.Part {
        /*
           The URI scheme we get is a content resolver and it's possible that the document shared is
           from the cloud. So we create a copy of the file onto our Scooped storage directory using
           standard Java IO which we have full access to. Then, we create a File object using that
           directory which can then be used to create the MultipartBody Part.
           */
        var fileName = ""
        selectedUri?.let { returnUri ->
            _application.contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }

        val mimeType = selectedUri?.let { returnUri ->
            _application.contentResolver.getType(returnUri)
        } ?: "application/*"

        val fileLocation =
            externalFileDir + File.separator.toString() + fileName

        val inputStream = _application.contentResolver.openInputStream(selectedUri!!)!!
        // selectedUri is checked to not be null before calling this function so it's safe to call !!
        val outputStream = FileOutputStream(File(fileLocation))
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) {
            outputStream.write(buf, 0, len)
        }
        outputStream.close()
        inputStream.close()

        val file = File(fileLocation)
        val body = RequestBody.create(MediaType.parse(mimeType), file)
        return MultipartBody.Part.createFormData("doc", file.name, body)
    }

    private fun uploadAttachmentForTodo(todoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.uploadDocumentForTodo(todoId, getFile())) {
                is OperationResult.Success -> {
                    scheduleEmailForTodoItem(todoId)
                }
                is OperationResult.Error -> {
                    _onAddTodoItemErrorMessage.postValue(
                        SingleLiveEvent(
                            result.message
                        )
                    )
                    _isAddingTodoItem.postValue(SingleLiveEvent(false))
                }
            }
        }
    }
}