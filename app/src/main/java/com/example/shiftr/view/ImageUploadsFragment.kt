package com.example.shiftr.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.shiftr.databinding.ImageUploadsFragmentBinding
import com.example.shiftr.viewmodel.ImageUploadsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageUploadsFragment : Fragment() {

    private lateinit var binding: ImageUploadsFragmentBinding
    private val viewModel by viewModels<ImageUploadsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ImageUploadsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}