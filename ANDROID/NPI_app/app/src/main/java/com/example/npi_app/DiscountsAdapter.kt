package com.example.npi_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DiscountsAdapter(
    private val discounts: List<Map<String, Any>>,
    private val maxPrice: Double, // Precio máximo que puede cubrir el descuento
    private val onSelectionChanged: (List<Map<String, Any>>) -> Unit
) : RecyclerView.Adapter<DiscountsAdapter.DiscountViewHolder>() {

    private val selectedItems = mutableSetOf<Int>()
    private var currentDiscountTotal = 0.0

    inner class DiscountViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.cbDiscount)
        val discountTitle: TextView = view.findViewById(R.id.tvDiscountTitle)
        val discountPrice: TextView = view.findViewById(R.id.tvDiscountPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_discount_layout, parent, false)
        return DiscountViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiscountViewHolder, position: Int) {
        val discount = discounts[position]
        val title = discount["title"] as? String ?: "Descuento"
        val discountValue = (discount["discount"] as? Double) ?: 0.0
        holder.discountTitle.text = title
        //Format the discountValue to "%.2f €
        holder.discountPrice.text = holder.itemView.context.getString(R.string.discount_price, discountValue)
        holder.checkBox.isChecked = selectedItems.contains(position)

        // Verificar si el descuento supera el límite del precio del producto
        val isDiscountSelectable = currentDiscountTotal < maxPrice || holder.checkBox.isChecked

        // Permitir selección solo si no excede el precio máximo
        holder.checkBox.isEnabled = isDiscountSelectable

        // Manejar la selección/deselección del descuento
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && isDiscountSelectable) {
                selectedItems.add(position)
                currentDiscountTotal += discountValue
            } else if (!isChecked) {
                selectedItems.remove(position)
                currentDiscountTotal -= discountValue
            }

            onSelectionChanged(selectedItems.map { discounts[it] })
            notifyDataSetChanged() // Refrescar el adaptador
        }
    }

    override fun getItemCount(): Int = discounts.size
}

