package com.example.npi_app

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


class TransactionsAdapter(private val transactions: List<Map<String, Any>>, private val context: Context) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvConcept: TextView = view.findViewById(R.id.tvConcept)
        val tvMethod: TextView = view.findViewById(R.id.tvMethod)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val transaction_data = transaction.get("data") as Map<*, *>
        val transaction_method = if(transaction["method"] == "money") {
            context.getString(R.string.transaction_money)
        } else {
            context.getString(R.string.transaction_points)
        }
        holder.tvConcept.text = if(transaction["method"] == "money") {
            context.getString(R.string.transaction_concept, transaction_data["concept"] as String)
        } else{
            context.getString(R.string.transaction_concept, transaction_data["category"] as String)
        }
        holder.tvMethod.text = context.getString(R.string.transaction_method, transaction_method)
        holder.tvAmount.text = if (transaction["method"] == "money") {
            context.getString(R.string.transaction_quantity_money, transaction["price"] as Double)
        } else {
            context.getString(R.string.transaction_quantity_points, transaction["points"] as Int)
        }
        holder.tvDate.text = context.getString(R.string.transaction_date, transaction["date"] as String)

        // Cambiar el color del fondo según el tipo de transacción
        val cardView = holder.itemView.findViewById<CardView>(R.id.cardViewTransaction)
        if (transaction["method"] == "money") {
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.money_transaction))
        } else {
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.points_transaction))
        }
    }

    override fun getItemCount() = transactions.size
}

