package com.example.shiftr.view.todo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shiftr.databinding.AddTodoItemFragmentBinding
import com.example.shiftr.viewmodel.ViewTodoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker

class AddTodoItemFragment(viewModel: ViewTodoViewModel) : BottomSheetDialogFragment() {

    private lateinit var binding: AddTodoItemFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddTodoItemFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            dateLayout.setEndIconOnClickListener {
                showDatePicker()
            }
            timeLayout.setEndIconOnClickListener {
                showTimePicker()
            }
        }
    }

    private fun showDatePicker() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker().build()
        datePicker.addOnPositiveButtonClickListener {
            Log.e(javaClass.simpleName, "Date: $it")
        }
        datePicker.show(parentFragmentManager, datePicker.toString())
    }

    private fun showTimePicker() {
        val timePicker = MaterialTimePicker.Builder().build()
        timePicker.addOnPositiveButtonClickListener {
            Log.e(javaClass.simpleName, "Time: ${timePicker.hour}:${timePicker.minute}")
        }
        timePicker.show(parentFragmentManager, timePicker.toString())
    }
}