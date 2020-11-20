package com.example.shiftr.view.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shiftr.R
import com.example.shiftr.data.TodoItem
import com.example.shiftr.databinding.ListItemTodoItemBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class TodoItemAdapter : RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {

    lateinit var listener: (TodoItem) -> Unit
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
            todoItemCard.setCardBackgroundColor(Color.parseColor(todo.getPriorityColor()))
            todoItemCard.setOnClickListener { listener(todo) }
            todoText.text = todo.itemText
            deadlineText.text = formatDate(todo.deadline)
            priorityText.text = todo.getPriorityText()

            if (todo.done) {
                statusImage.setImageResource(R.drawable.outline_done_24)

                todoText.paintFlags = todoText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                deadlineText.paintFlags = deadlineText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                priorityText.paintFlags = priorityText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            } else {
                statusImage.setImageResource(R.drawable.outline_clear_24)

                todoText.paintFlags = todoText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                deadlineText.paintFlags = deadlineText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                priorityText.paintFlags = priorityText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }

    private val formatter = DateTimeFormatter.ofPattern("yyyy MM dd", Locale.ENGLISH)
    private fun formatDate(date: String): String {
        val parsedDate = LocalDate.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return parsedDate.format(formatter)
    }

    inner class TodoItemViewHolder(val binding: ListItemTodoItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}