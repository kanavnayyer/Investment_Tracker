package com.awesome.investmenttracker.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.awesome.investmenttracker.R
import com.awesome.investmenttracker.databinding.FragmentTransactionsBinding
import com.awesome.investmenttracker.view.adapters.TransactionAdapter
import com.awesome.investmenttracker.viewModels.TransactionViewModel

class TransactionsFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsBinding
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showData()
        setupRecyclerView()
        setupSelectionListeners()
        setupSearchListeners()

        viewModel.filteredTransactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions) {
                binding.recyclerView.post {
                    binding.recyclerView.scrollToPosition(0) 
                }
            }
        }
    }

    private fun showData() {
        val key = getString(R.string.selectedtimefilter)
        val selectedFilter = arguments?.getString(key)

        if (selectedFilter.equals(getString(R.string.v))) {
            selectCategory(binding.allDropdown, binding.categoryDropdown)
            viewModel.loadTransactions(getString(R.string.all_))
        } else {
            selectedFilter?.let {
                viewModel.loadTransactions(it.toLowerCase())
            }
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }

    private fun setupSelectionListeners() {
        binding.categoryDropdown.setOnClickListener {
            if (binding.descriptionInput.text.isNotEmpty()) binding.descriptionInput.text.clear()
            selectCategory(binding.categoryDropdown, binding.allDropdown)
            viewModel.loadTransactions(getString(R.string.month))
        }

        binding.allDropdown.setOnClickListener {
            if (binding.descriptionInput.text.isNotEmpty()) binding.descriptionInput.text.clear()
            selectCategory(binding.allDropdown, binding.categoryDropdown)
            viewModel.loadTransactions(getString(R.string.all_))
        }
    }

    private fun setupSearchListeners() {
        binding.descriptionInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterTransactions()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun selectCategory(selected: View, other: View) {
        selected.setBackgroundResource(R.drawable.bg_selected)
        other.setBackgroundResource(R.drawable.bg_unselected)
    }

    private fun filterTransactions() {
        val query = binding.descriptionInput.text.toString().trim()
        viewModel.searchTransactions(query)
    }

    override fun onResume() {
        super.onResume()
        showData()
    }
}
