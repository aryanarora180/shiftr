package com.example.shiftr.view.todo

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.shiftr.databinding.ViewTodoItemsFragmentBinding
import com.example.shiftr.viewmodel.ViewTodoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewTodoFragment : Fragment() {

    private lateinit var binding: ViewTodoItemsFragmentBinding
    private val args by navArgs<ViewTodoFragmentArgs>()
    private val viewModel by viewModels<ViewTodoViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ViewTodoItemsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val color = Color.parseColor(args.todo.color)

        requireActivity().window.statusBarColor = color
        with(binding) {
            todoFrame.backgroundTintList = ColorStateList.valueOf(color)
            nameText.text = args.todo.title
            descriptionText.text = args.todo.description
        }
    }
}