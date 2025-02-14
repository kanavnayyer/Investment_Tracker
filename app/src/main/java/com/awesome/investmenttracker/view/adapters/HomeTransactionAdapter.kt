package com.awesome.investmenttracker.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.awesome.investmenttracker.databinding.HomeTransBinding
import com.awesome.investmenttracker.model.entities.TransactionResponse

class HomeTransactionAdapter : RecyclerView.Adapter<HomeTransactionAdapter.HomeTransactionViewHolder>() {

    private var transactions: List<TransactionResponse> = emptyList()

    inner class HomeTransactionViewHolder(private val binding: HomeTransBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionResponse) {
            binding.transaction = transaction
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeTransactionViewHolder {
        val binding = HomeTransBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeTransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeTransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    fun submitList(newTransactions: List<TransactionResponse>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
