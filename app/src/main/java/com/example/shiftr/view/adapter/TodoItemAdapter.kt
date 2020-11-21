package com.example.shiftr.view.adapter

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shiftr.R
import com.example.shiftr.data.TodoItem
import com.example.shiftr.databinding.ListItemTodoItemBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TodoItemAdapter : RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {

    lateinit var deleteListener: (TodoItem) -> Unit
    lateinit var actionListener: (TodoItem) -> Unit

    var data = listOf<TodoItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
        return TodoItemViewHolder(
            ListItemTodoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        val todo = data[position]

        with(holder.binding) {
            todoItemCard.setOnLongClickListener {
                deleteListener(todo)
                true
            }
            todoText.text = todo.itemText
            deadlineText.text = formatDate(todo.deadline)
            priorityText.text = todo.getPriorityText()
            priorityText.setTextColor(Color.parseColor(todo.getPriorityColor()))

            if (todo.done) {
                statusImage.setImageResource(R.drawable.outline_clear_24)

                todoText.paintFlags = todoText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                deadlineText.paintFlags = deadlineText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                priorityText.paintFlags = priorityText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                statusImage.setImageResource(R.drawable.outline_done_24)

                todoText.paintFlags = todoText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                deadlineText.paintFlags = deadlineText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                priorityText.paintFlags = priorityText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            statusImage.setOnClickListener { actionListener(todo) }
        }
    }

    private val dateFormatter = DateTimeFormatter.ofPattern("E, MMM dd", Locale.ENGLISH)
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
    private fun formatDate(date: String): String {
        val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return "Due ${parsedDate.format(dateFormatter)} at ${parsedDate.format(timeFormatter)}"
    }

    inner class TodoItemViewHolder(val binding: ListItemTodoItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}