package com.example.shiftr.view.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shiftr.data.Todo
import com.example.shiftr.databinding.ListItemTodoBinding

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    lateinit var listener: (Todo) -> Unit

    var data = listOf<Todo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(ListItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = data[position]

        with(holder.binding) {
            todoCard.setCardBackgroundColor(Color.parseColor(todo.color))
            todoCard.setOnClickListener { listener(todo) }
            titleText.text = todo.title
            descriptionText.text = todo.description
        }
    }

    inner class TodoViewHolder(val binding: ListItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root)
}