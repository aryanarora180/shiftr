package com.example.shiftr.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shiftr.BuildConfig
import com.example.shiftr.data.DashboardResponse
import com.example.shiftr.data.OperationResult
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.model.AppDataSource
import com.example.shiftr.model.DataStoreUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@SuppressLint("NullSafeMutableLiveData")
class DashboardViewModel @ViewModelInject constructor(
    private val repository: AppDataSource,
    private val dataStoreUtils: DataStoreUtils,
    application: Application
) : AndroidViewModel(application) {

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

    private val _onDashboardSave = MutableLiveData<SingleLiveEvent<Uri>>()
    val onDashboardSave: LiveData<SingleLiveEvent<Uri>>
        get() = _onDashboardSave

    private val externalFileDir = application.getExternalFilesDir(null).toString()
    fun saveDashboardScreenshot(screenshot: Bitmap) {
        val fileLocation = externalFileDir + File.separator.toString() + "dashboard.png"

        try {
            FileOutputStream(fileLocation).use { out ->
                screenshot.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            _onDashboardSave.postValue(
                SingleLiveEvent(
                    FileProvider.getUriForFile(
                        getApplication(),
                        "${BuildConfig.APPLICATION_ID}.provider",
                        File(fileLocation)
                    )
                )
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun signOut() = dataStoreUtils.signOut()

    init {
        loadDashboard()
    }
}