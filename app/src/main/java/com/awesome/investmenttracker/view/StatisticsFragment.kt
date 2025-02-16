package com.awesome.investmenttracker.view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.awesome.investmenttracker.R
import com.awesome.investmenttracker.databinding.FragmentStatiticsBinding
import com.awesome.investmenttracker.model.entities.CategoryTotal
import com.awesome.investmenttracker.viewModels.TransactionViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class StatisticsFragment : Fragment() {

    private val viewModel: TransactionViewModel by viewModels()
    private var _binding: FragmentStatiticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var radioGroup: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatiticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioGroup = binding.root.findViewById(R.id.rgTimeFilter)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedButton = view.findViewById<RadioButton>(checkedId)

            binding.scrollView.scrollTo(0, 0)

            when (selectedButton.id) {
                R.id.rbExpense -> {
                    val filteredExpense = viewModel.categoryTotals.value?.filter { it.type == getString(R.string.expense) }
                    filteredExpense?.let {
                        setupPieChart(it, getString(R.string.expense))
                        setupLegend(it, it.sumOf { category -> category.total }, generateColors(it.size))
                    }
                }

                R.id.rbWeek -> {
                    val filteredIncome = viewModel.categoryTotals.value?.filter { it.type == getString(R.string.income) }
                    filteredIncome?.let {
                        setupPieChart(it, getString(R.string.income))
                        setupLegend(it, it.sumOf { category -> category.total }, generateColors(it.size))
                    }
                }
            }
        }

        viewModel.categoryTotals.observe(viewLifecycleOwner) { categoryTotals ->
            if (radioGroup.checkedRadioButtonId == R.id.rbExpense) {
                val filteredExpense = categoryTotals.filter { it.type == getString(R.string.expense) }
                setupPieChart(filteredExpense, getString(R.string.expense))
                setupLegend(filteredExpense, filteredExpense.sumOf { category -> category.total }, generateColors(filteredExpense.size))
            } else {
                val filteredIncome = categoryTotals.filter { it.type == getString(R.string.income) }
                setupPieChart(filteredIncome, getString(R.string.income))
                setupLegend(filteredIncome, filteredIncome.sumOf { category -> category.total }, generateColors(filteredIncome.size))
            }
        }
    }




    private fun setupPieChart(data: List<CategoryTotal>, chartType: String) {
        if (data.isEmpty()) {
            binding.pieChart.clear()
            binding.pieChart.setNoDataText(getString(R.string.no_investments_to_display))
            return
        }

        val sortedCategories = data.sortedByDescending { it.total }
        val totalInvestment = sortedCategories.sumOf { it.total }

        val entries = sortedCategories.map { PieEntry(it.total.toFloat(), it.category) }
        val colors = generateColors(sortedCategories.size)

        val dataSet = PieDataSet(entries, "").apply {
            this.colors = colors
            valueTextColor = Color.BLACK
            valueTextSize = 14f
            setDrawValues(false)
        }

        val pieData = PieData(dataSet)

        binding.pieChart.apply {
            this.data = pieData
            description.isEnabled = false
            setUsePercentValues(false)
            setDrawEntryLabels(false)
            legend.isEnabled = false
            isRotationEnabled = false
            isHighlightPerTapEnabled = false
            centerText = "₹${totalInvestment.toInt()}"
            animateY(1000)
            invalidate()
            setHoleColor(Color.TRANSPARENT)
            setCenterTextTypeface(Typeface.create(context.getString(R.string.sans_serif_medium), Typeface.BOLD))
        }

        setupLegend(sortedCategories, totalInvestment, colors)
    }

    private fun setupLegend(data: List<CategoryTotal>, totalInvestment: Double, colors: List<Int>) {
        binding.legendContainer.removeAllViews()

        data.forEachIndexed { index, categoryTotal ->
            val legendItem = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_legend, binding.legendContainer, false)

            val colorView = legendItem.findViewById<ImageView>(R.id.legendColor)
            val textView = legendItem.findViewById<TextView>(R.id.legendText)
            val progressBar = legendItem.findViewById<ProgressBar>(R.id.legendProgressBar)
            val amountTextView = legendItem.findViewById<TextView>(R.id.legendAmount)

            val categoryColor = colors[index % colors.size]
            colorView.setColorFilter(categoryColor)
            textView.text = categoryTotal.category
            amountTextView.text = viewModel.formatAmount(categoryTotal.total)

            progressBar.max = totalInvestment.toInt()
            progressBar.progress = categoryTotal.total.toInt()

            val progressDrawable = progressBar.progressDrawable?.mutate()
            progressDrawable?.setTint(categoryColor)
            progressBar.progressDrawable = progressDrawable

            binding.legendContainer.addView(legendItem)
        }
    }

    private fun generateColors(size: Int): List<Int> {
        val predefinedColors = listOf(
            Color.BLUE, Color.GREEN, Color.RED, Color.MAGENTA, Color.CYAN, Color.YELLOW
        )
        return if (size <= predefinedColors.size) {
            predefinedColors.take(size)
        } else {
            List(size) { Color.rgb((0..255).random(), (0..255).random(), (0..255).random()) }
        }
    }

    override fun onResume() {
        super.onResume()

    }
}