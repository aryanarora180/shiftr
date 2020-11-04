package com.example.shiftr.view

import android.view.View
import com.example.shiftr.data.SingleLiveEvent
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(text: String) = Snackbar.make(
    this,
    text,
    Snackbar.LENGTH_LONG
).show()

fun View.showSnackbar(text: SingleLiveEvent<String>) {
    text.getContentIfNotHandled()?.let {
        Snackbar.make(
            this,
            it,
            Snackbar.LENGTH_LONG
        ).show()
    }
}