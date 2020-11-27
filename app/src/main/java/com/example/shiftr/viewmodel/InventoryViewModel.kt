package com.example.shiftr.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shiftr.data.InventoryItem
import com.example.shiftr.data.OperationResult
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.model.AppDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream


@SuppressLint("NullSafeMutableLiveData")
class InventoryViewModel @ViewModelInject constructor(
    private val repository: AppDataSource,
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

    fun updateInventoryItemQuantity(inventoryItem: InventoryItem, toIncrease: Boolean) {
        viewModelScope.launch {
            _isDataLoading.postValue(true)
            when (val result =
                repository.updateInventoryItemQuantity(
                    inventoryItem.id,
                    if (toIncrease) (inventoryItem.quantity + 1)
                    else (inventoryItem.quantity - 1)
                )) {
                is OperationResult.Success -> {
                    loadInventory()
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

    fun deleteInventoryItem(inventoryItem: InventoryItem) {
        viewModelScope.launch {
            _isDataLoading.postValue(true)
            when (val result =
                repository.deleteInventoryItem(
                    inventoryItem.id,
                )) {
                is OperationResult.Success -> {
                    loadInventory()
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

    var selectedUri: Uri? = null

    private val externalFileDir = application.getExternalFilesDir(null).toString()
    fun getFile(): MultipartBody.Part? {
        if (selectedUri == null) {
            return null
        } else {
            //Using standard Android functions to get the file name from the URI and the mime type.
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
            } ?: "image/*"

            val fileLocation =
                externalFileDir + File.separator.toString() + fileName

            /*
           The URI scheme we get is a content resolver and it's possible that the document shared is
           from the cloud. So we create a copy of the file onto our Scooped storage directory using
           standard Java IO which we have full access to. Then, we create a File object using that
           directory which can then be used to create the MultipartBody Part.
           */
            val inputStream = _application.contentResolver.openInputStream(selectedUri!!)!!
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
            return MultipartBody.Part.createFormData("upload", file.name, body)
        }
    }

    init {
        loadInventory()
    }
}