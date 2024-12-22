package com.example.npi_app

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VouchersAdapter(
    private val vouchers: List<Map<String, Any>>,
    private val onClick: (Map<String, Any>) -> Unit
) : RecyclerView.Adapter<VouchersAdapter.VoucherViewHolder>() {

    inner class VoucherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvConcept: TextView = itemView.findViewById(R.id.tvConcept)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvDetails: TextView = itemView.findViewById(R.id.tvDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_voucher, parent, false)
        return VoucherViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        val voucher = vouchers[position]
        holder.tvConcept.text = voucher["concept"] as String
        holder.tvDate.text = voucher["date"] as String
        holder.itemView.setOnClickListener { onClick(voucher) }
        val details = when (voucher["concept"] as String) {
            //"Deportes" -> "Lugar: ${voucher["place"]}\nHora: ${voucher["start"]} - ${voucher["end"]}\nPista: ${voucher["court"]}"
            // Mejor usando un string resource:
            "Deportes" -> holder.itemView.context.getString(
                R.string.voucher_details_sports,
                voucher["place"],
                voucher["start"],
                voucher["end"],
                voucher["court"]
            )
            else -> ""
        }

        holder.tvDetails.text = details

        when (voucher["concept"] as String) {
            "Comedor" -> holder.itemView.setBackgroundColor(Color.parseColor("#BBDEFB")) // Azul claro
            "Deportes" -> holder.itemView.setBackgroundColor(Color.parseColor("#FFCC80")) // Naranja claro
            else -> holder.itemView.setBackgroundColor(Color.parseColor("#E0E0E0")) // Gris claro
        }

    }

    override fun getItemCount(): Int = vouchers.size
}
