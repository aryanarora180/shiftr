package com.example.shiftr.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shiftr.data.TodoItem
import com.example.shiftr.databinding.ListItemPriorityTodoBinding
import com.example.shiftr.databinding.ListItemTodoBinding

class TodoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var listener: (TodoItem) -> Unit

    var data = listOf<TodoItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int) = data[position].isPriority

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TodoItem.IS_PRIORITY) PriorityTodoViewHolder(
            ListItemPriorityTodoBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        ) else TodoViewHolder(ListItemTodoBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TodoItem.IS_PRIORITY) {
            with(holder as PriorityTodoViewHolder) {
                with(data[position]) {
                    with(binding) {
                        titleText.text = title
                        descriptionText.text = description
                        notificationEnabledImage.visibility =
                            if (notificationEnabled == 1) View.VISIBLE else View.GONE
                    }
                }
            }
        } else {
            with(holder as TodoViewHolder) {
                with(data[position]) {
                    with(binding) {
                        titleText.text = title
                        descriptionText.text = description
                        notificationEnabledImage.visibility =
                            if (notificationEnabled == 1) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    inner class TodoViewHolder(val binding: ListItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class PriorityTodoViewHolder(val binding: ListItemPriorityTodoBinding) :
        RecyclerView.ViewHolder(binding.root)
}