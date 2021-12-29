package com.example.expensetrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(private val transactionsList: ArrayList<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_transaction, parent, false)
        return TransactionHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val currentItem = transactionsList[position]
        holder.amount.text = currentItem.amount.toString()
        holder.note.text = currentItem.note
    }

    override fun getItemCount(): Int {
        return transactionsList.size
    }

    class TransactionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amount: TextView = itemView.findViewById(R.id.amountTV)
        val note: TextView = itemView.findViewById(R.id.noteTV)

    }
}