package com.example.shiftr.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.shiftr.model.AppDataSource

class ProfileViewModel @ViewModelInject constructor(
    private val repository: AppDataSource
) : ViewModel() {



}