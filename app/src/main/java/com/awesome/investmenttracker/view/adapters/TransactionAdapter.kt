package com.awesome.investmenttracker.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.awesome.investmenttracker.databinding.TransItemBinding
import com.awesome.investmenttracker.model.entities.TransactionResponse

class TransactionAdapter :
    ListAdapter<TransactionResponse, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = TransItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    class TransactionViewHolder(private val binding: TransItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionResponse) {
            binding.transaction = transaction
            binding.executePendingBindings()
        }
    }
}

class TransactionDiffCallback : DiffUtil.ItemCallback<TransactionResponse>() {
    override fun areItemsTheSame(oldItem: TransactionResponse, newItem: TransactionResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TransactionResponse, newItem: TransactionResponse): Boolean {
        return oldItem == newItem
    }
}
