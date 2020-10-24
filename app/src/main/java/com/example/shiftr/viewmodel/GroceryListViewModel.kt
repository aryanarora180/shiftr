package com.example.shiftr.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.shiftr.model.AppDataSource

class GroceryListViewModel @ViewModelInject constructor(
    private val repository: AppDataSource
) : ViewModel() {



}