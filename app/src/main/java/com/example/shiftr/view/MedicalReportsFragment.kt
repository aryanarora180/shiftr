package com.example.shiftr.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.shiftr.R
import com.example.shiftr.databinding.MedicalReportsFragmentBinding
import com.example.shiftr.viewmodel.MedicalReportsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicalReportsFragment : Fragment() {

    private lateinit var binding: MedicalReportsFragmentBinding
    private val viewModel by viewModels<MedicalReportsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MedicalReportsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}