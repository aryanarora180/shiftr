package com.example.shiftr.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shiftr.data.InventoryItem
import com.example.shiftr.databinding.ListItemInventoryBinding

class InventoryAdapter : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    lateinit var deleteListener: (InventoryItem) -> Unit

    var data = listOf<InventoryItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        return InventoryViewHolder(ListItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = data[position]

        with(holder.binding) {
            inventoryCard.setOnLongClickListener {
                deleteListener(item)
                true
            }
            nameText.text = item.name
            quantityText.text = "${item.quantity} ${item.unit}"
            categoryText.text = item.category
        }
    }

    inner class InventoryViewHolder(val binding: ListItemInventoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}