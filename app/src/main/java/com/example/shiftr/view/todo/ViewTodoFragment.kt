package com.example.shiftr.view.todo

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shiftr.R
import com.example.shiftr.data.SingleLiveEvent
import com.example.shiftr.data.TodoItem
import com.example.shiftr.databinding.ViewTodoItemsFragmentBinding
import com.example.shiftr.view.SpringyRecycler
import com.example.shiftr.view.adapter.TodoItemAdapter
import com.example.shiftr.view.showSnackbar
import com.example.shiftr.viewmodel.ToDoListViewModel
import com.example.shiftr.viewmodel.ViewTodoViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ViewTodoFragment : Fragment() {

    private lateinit var binding: ViewTodoItemsFragmentBinding
    private val args by navArgs<ViewTodoFragmentArgs>()
    private val viewModel by viewModels<ViewTodoViewModel>()
    private val todoViewModel by activityViewModels<ToDoListViewModel>()

    private val todoItemsAdapter = TodoItemAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

            addTodoItemFab.backgroundTintList = ColorStateList.valueOf(color)
            addTodoItemFab.setOnClickListener {
                AddTodoItemBottomSheetFragment(viewModel).show(
                    childFragmentManager,
                    "add-todo-item"
                )
            }

            deleteImage.setOnClickListener { showDeleteConfirmation() }
            shareImage.setOnClickListener {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, viewModel.getShareText())
                sendIntent.type = "text/plain"
                val shareIntent = Intent.createChooser(sendIntent, "Share todo list for ${args.todo}")
                startActivity(shareIntent)
            }
        }

        todoItemsAdapter.deleteListener = { todoItem ->
            showDeleteItemConfirmation(todoItem)
        }
        todoItemsAdapter.actionListener = { todoItem ->
            viewModel.toggleTodoDone(todoItem)
        }

        binding.todoItemsRecycler.apply {
            edgeEffectFactory =
                SpringyRecycler.springEdgeEffectFactory<TodoItemAdapter.TodoItemViewHolder>()
            adapter = todoItemsAdapter
        }

        with(viewModel) {
            todoItems.observe(viewLifecycleOwner, todoItemsObserver)
            isEmptyList.observe(viewLifecycleOwner, isEmptyListObserver)
            onMessageError.observe(viewLifecycleOwner, onErrorObserver)
            isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)

            isDeletingTodo.observe(viewLifecycleOwner, isDeletingTodoObserver)
            onDeleteTodoErrorMessage.observe(viewLifecycleOwner, onDeleteTodoErrorMessageObserver)
            onDeleteTodoSuccess.observe(viewLifecycleOwner, onDeleteTodoSuccessObserver)
        }
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        with(binding) {
            if (isLoading) {
                todoProgress.visibility = View.VISIBLE
                todoItemsRecycler.visibility = View.GONE
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
                todoItemsRecycler.visibility = View.INVISIBLE
                noTodoImage.visibility = View.VISIBLE
                noTodoText.visibility = View.VISIBLE
            } else {
                noTodoImage.visibility = View.GONE
                noTodoText.visibility = View.GONE
            }
        }
    }

    private val todoItemsObserver = Observer<List<TodoItem>> { items ->
        with(binding) {
            todoItemsRecycler.visibility = View.VISIBLE

            if (typeChipGroup.checkedChipId == R.id.chip_done) {
                todoItemsAdapter.data = items.filter { it.done }
            } else {
                todoItemsAdapter.data = items.filter { !it.done }
            }

            typeChipGroup.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId == R.id.chip_done) {
                    todoItemsAdapter.data = items.filter { it.done }
                } else {
                    todoItemsAdapter.data = items.filter { !it.done }
                }
            }
        }
    }

    private val onErrorObserver = Observer<SingleLiveEvent<String>> { error ->
        requireView().showSnackbar(error)
    }

    private val isDeletingTodoObserver = Observer<Boolean> {
        with(binding) {
            if (it) {
                todoConstraint.visibility = View.GONE
                todoProgress.visibility = View.VISIBLE
            } else {
                todoConstraint.visibility = View.VISIBLE
                todoProgress.visibility = View.GONE
            }
        }
    }

    private val onDeleteTodoErrorMessageObserver = Observer<SingleLiveEvent<String>> {
        requireView().showSnackbar(it)
    }

    private val onDeleteTodoSuccessObserver = Observer<SingleLiveEvent<Boolean>> {
        it.getContentIfNotHandled()?.let { isSuccess ->
            if (isSuccess) {
                todoViewModel.loadTodos()
                findNavController().navigate(R.id.action_viewTodoFragment_to_toDoListFragment)
            }
        }
    }

    private fun showDeleteConfirmation() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Delete task")
            setMessage("Are you sure you want to delete this task?")
            setPositiveButton("Yes") { _, _ ->
                viewModel.deleteTodo()
            }
            setNeutralButton("Cancel") { _, _ -> /* Do nothing */ }
            show()
        }
    }

    private fun showDeleteItemConfirmation(todoItem: TodoItem) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Delete todo")
            setMessage("Are you sure you want to delete todo \"${todoItem.itemText}\"?")
            setPositiveButton("Yes") { _, _ ->
                viewModel.deleteTodoItem(todoItem)
            }
            setNeutralButton("Cancel") { _, _ -> /* Do nothing */ }
            show()
        }
    }
}