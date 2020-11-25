package com.example.shiftr.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shiftr.data.InventoryItem
import com.example.shiftr.databinding.ListItemInventoryBinding

class InventoryAdapter : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    lateinit var increaseQuantityListener: (InventoryItem) -> Unit
    lateinit var decreaseQuantityListener: (InventoryItem) -> Unit
    lateinit var deleteListener: (InventoryItem) -> Unit

    var data = listOf<InventoryItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        return InventoryViewHolder(
            ListItemInventoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = data[position]

        with(holder.binding) {
            addQtyImage.setOnClickListener { increaseQuantityListener(item) }
            decQtyImage.setOnClickListener { decreaseQuantityListener(item) }
            inventoryCard.setOnLongClickListener {
                deleteListener(item)
                true
            }

            nameText.text = item.name
            quantityText.text = "${item.quantity}"
            item.getUnitText().let {
                quantityUnitText.visibility = if (it.isBlank()) View.GONE else View.VISIBLE
                quantityUnitText.text = it
            }
            categoryText.text = item.category
        }
    }

    inner class InventoryViewHolder(val binding: ListItemInventoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}