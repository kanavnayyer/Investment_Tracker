package com.awesome.investmenttracker.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.awesome.investmenttracker.R
import com.awesome.investmenttracker.databinding.FragmentHomeBinding
import com.awesome.investmenttracker.view.adapters.HomeTransactionAdapter
import com.awesome.investmenttracker.viewModels.TransactionViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var adapter: HomeTransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HomeTransactionAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
        }

        viewModel.loadTransactions(getString(R.string.today_))

        binding.rgTimeFilter.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbToday -> viewModel.loadTransactions(getString(R.string.today_))
                R.id.rbWeek -> viewModel.loadTransactions(getString(R.string.week_))
                R.id.rbMonth -> viewModel.loadTransactions(getString(R.string.month_))
                R.id.rbYear -> viewModel.loadTransactions(getString(R.string.year_))
            }
        }

        viewModel.filteredTransactions.observe(viewLifecycleOwner) { transactions ->

            adapter.submitList(transactions)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTransactions(getString(R.string.today_))
    }

}


