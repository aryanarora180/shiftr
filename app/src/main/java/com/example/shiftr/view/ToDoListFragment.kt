package com.example.shiftr.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.data.TodoItem
import com.example.shiftr.databinding.ToDoListFragmentBinding
import com.example.shiftr.view.adapter.TodoAdapter
import com.example.shiftr.viewmodel.ToDoListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ToDoListFragment : Fragment() {

    private lateinit var binding: ToDoListFragmentBinding
    private val viewModel by viewModels<ToDoListViewModel>()

    private val todoAdapter = TodoAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ToDoListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todoAdapter.listener = { todoItem ->
            // TODO: Handle Todo click
        }
        binding.todoRecycler.adapter = todoAdapter

        viewModel.isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
        viewModel.isEmptyList.observe(viewLifecycleOwner, isEmptyListObserver)
        viewModel.todos.observe(viewLifecycleOwner, todoObserver)
        viewModel.onMessageError.observe(viewLifecycleOwner, onErrorObserver)
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        with(binding) {
            if (isLoading) {
                todoProgress.visibility = View.VISIBLE
                todoRecycler.visibility = View.GONE
            } else {
                todoProgress.visibility = View.GONE
            }
        }
    }

    private val isEmptyListObserver = Observer<Boolean> { isEmpty ->
        with(binding) {
            if (isEmpty) {
                todoRecycler.visibility = View.GONE
                noTodoImage.visibility = View.VISIBLE
                noTodoText.visibility = View.VISIBLE
            } else {
                todoRecycler.visibility = View.VISIBLE
                noTodoImage.visibility = View.GONE
                noTodoText.visibility = View.GONE
            }
        }
    }

    private val todoObserver = Observer<List<TodoItem>> { todos ->
        with(binding) {
            todoRecycler.visibility = View.VISIBLE
            todoAdapter.data = todos
        }
    }

    private val onErrorObserver = Observer<SingleLiveEvent<String>> { error ->
        requireView().showSnackbar(error)
    }
}