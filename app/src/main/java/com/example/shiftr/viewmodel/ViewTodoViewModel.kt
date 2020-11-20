package com.example.shiftr.viewmodel

import android.annotation.SuppressLint
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.shiftr.model.AppDataSource

@SuppressLint("NullSafeMutableLiveData")
class ViewTodoViewModel @ViewModelInject constructor(
    private val repository: AppDataSource,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {



}