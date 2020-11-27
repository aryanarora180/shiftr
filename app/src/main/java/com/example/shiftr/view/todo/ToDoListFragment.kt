package com.example.shiftr.view.todo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.shiftr.R
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.data.Todo
import com.example.shiftr.databinding.ToDoListFragmentBinding
import com.example.shiftr.view.SpringyRecycler
import com.example.shiftr.view.adapter.TodoAdapter
import com.example.shiftr.view.showSnackbar
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
    ): View {
        binding = ToDoListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black)

        todoAdapter.listener = { todoItem ->
            val action = ToDoListFragmentDirections.actionToDoListFragmentToViewTodoFragment(todoItem)
            findNavController().navigate(action)
        }
        binding.todoRecycler.apply {
            edgeEffectFactory =
                SpringyRecycler.springEdgeEffectFactory<TodoAdapter.TodoViewHolder>()
            adapter = todoAdapter
        }
        binding.addTodoFab.setOnClickListener {
            AddTodoBottomSheetFragment(viewModel).show(childFragmentManager, "add-todo")
        }

        with(viewModel) {
            todos.observe(viewLifecycleOwner, todoObserver)
            isEmptyList.observe(viewLifecycleOwner, isEmptyListObserver)
            onMessageError.observe(viewLifecycleOwner, onErrorObserver)
            isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
        }
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        with(binding) {
            if (isLoading) {
                todoProgress.visibility = View.VISIBLE
                todoRecycler.visibility = View.GONE
                noTodoImage.visibility = View.GONE
                noTodoText.visibility = View.GONE
            } else {
                todoProgress.visibility = View.GONE
            }
        }
    }

    private val isEmptyListObserver = Observer<Boolean> { isEmpty ->
        with(binding) {
            if (isEmpty) {
                todoRecycler.visibility = View.INVISIBLE
                noTodoImage.visibility = View.VISIBLE
                noTodoText.visibility = View.VISIBLE
            } else {
                noTodoImage.visibility = View.GONE
                noTodoText.visibility = View.GONE
            }
        }
    }

    private val todoObserver = Observer<List<Todo>> { todos ->
        with(binding) {
            todoRecycler.visibility = View.VISIBLE
            todoAdapter.data = todos
        }
    }

    private val onErrorObserver = Observer<SingleLiveEvent<String>> { error ->
        requireView().showSnackbar(error)
    }
}