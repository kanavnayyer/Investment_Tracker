package com.awesome.investmenttracker.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class StatisticsFragment : Fragment() {

    private val viewModel: TransactionViewModel by viewModels()
    private var _binding: FragmentStatiticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatiticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.categoryTotals.observe(viewLifecycleOwner) { categoryTotals ->
            setupPieChart(categoryTotals)
        }
    }

    private fun setupPieChart(data: List<CategoryTotal>) {
        val entries = data.filter { it.total > 0 }
            .map { PieEntry(it.total) }

        if (entries.isEmpty()) {
            binding.pieChart.clear()
            binding.pieChart.setNoDataText(getString(R.string.no_investments_to_display))
            return
        }

        val colors = listOf(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW, Color.MAGENTA)

        val dataSet = PieDataSet(entries, "").apply {
            this.colors = colors
            valueTextColor = Color.BLACK
            valueTextSize = 14f
            setDrawValues(true)
        }

        val pieData = PieData(dataSet).apply {
            setValueFormatter(object : ValueFormatter() {
                private val decimalFormat = DecimalFormat("##.##")
                override fun getFormattedValue(value: Float): String {
                    return if (value > 0) "${decimalFormat.format(value)}%" else ""
                }
            })
        }

        binding.pieChart.apply {
            this.data = pieData
            description.isEnabled = false
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            legend.isEnabled = false
            isRotationEnabled = false
            isHighlightPerTapEnabled = false
            animateY(1000)
            invalidate()
        }

        setupLegend(data, colors)
    }

    private fun setupLegend(data: List<CategoryTotal>, colors: List<Int>) {
        val legendContainer = binding.legendContainer
        legendContainer.removeAllViews()

        data.filter { it.total > 0 }.forEachIndexed { index, categoryTotal ->
            val legendItem = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_legend, legendContainer, false) as LinearLayout

            val colorView = legendItem.findViewById<View>(R.id.legendColor)
            val textView = legendItem.findViewById<TextView>(R.id.legendText)

            colorView.setBackgroundColor(colors[index])
            textView.text = categoryTotal.category

            legendContainer.addView(legendItem)
        }
    }

}
