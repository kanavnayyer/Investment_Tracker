package com.awesome.investmenttracker.view.adapters


import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.awesome.investmenttracker.R
import com.awesome.investmenttracker.model.entities.TransactionResponse
import com.awesome.investmenttracker.util.Constants.amountColor
import com.awesome.investmenttracker.util.Constants.doubleText

import com.awesome.investmenttracker.util.Constants.formattedAmount
import com.awesome.investmenttracker.util.Constants.formattedAmountNoc
import com.awesome.investmenttracker.util.Constants.formattedTime
import com.awesome.investmenttracker.util.Constants.transactionBackground
import com.awesome.investmenttracker.util.Constants.transactionTypeBackground
import com.awesome.investmenttracker.util.Constants.transactionTypeIcon
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.sign

object BindingAdapters {

    @JvmStatic
    @BindingAdapter(doubleText)
    fun setDoubleText(view: TextView, value: Double?) {
        view.text = value?.toString() ?: view.context.getString(R.string._0_0)
    }

    @JvmStatic
    @BindingAdapter(amountColor)
    fun setAmountColor(view: TextView, transaction: TransactionResponse?) {
        transaction?.let {
            val colorRes = if (it.type == view.context.getString(R.string.expens_e)) R.color.red else R.color.green
            view.setTextColor(ContextCompat.getColor(view.context, colorRes))
        }
    }

    @JvmStatic
    @BindingAdapter(formattedAmount)
    fun setFormattedAmount(view: TextView, transaction: TransactionResponse?) {
        transaction?.let {
            val sign = if (it.type == view.context.getString(R.string.expens_e)) "- ₹ " else "+ ₹ "

            val formattedAmount = NumberFormat.getNumberInstance(Locale("en", "IN")).format(it.amount)

            view.text = "$sign$formattedAmount"
        }
    }



    @JvmStatic
    @BindingAdapter(formattedAmountNoc)
    fun setFormattedAmountNoc(view: TextView, amount: Double?) {
        amount?.let {
            val formattedAmount = NumberFormat.getNumberInstance(Locale("en", "IN")).format(it)
            view.text = "₹ $formattedAmount"
        }
    }
    @JvmStatic
    @BindingAdapter(formattedTime)
    fun setFormattedTime(view: TextView, time: String?) {
        time?.let {
            val inputFormat = SimpleDateFormat(view.context.getString(R.string.hh_mm_ss), Locale.getDefault())
            val outputFormat = SimpleDateFormat(view.context.getString(R.string.hh_mm_aa), Locale.getDefault())
            try {
                val date = inputFormat.parse(it)
                view.text = date?.let { outputFormat.format(it) } ?: it
            } catch (e: Exception) {
                view.text = it
            }
        }
    }

    @JvmStatic
    @BindingAdapter(transactionTypeIcon)
    fun setTransactionTypeIcon(view: ImageView, type: String?) {
        type?.let {
            view.setImageResource(
                if (it == view.context.getString(R.string.expens_e)) R.drawable.baseline_arrow_downward_24
                else R.drawable.baseline_arrow_upward_24
            )
        }
    }

    @JvmStatic
    @BindingAdapter(transactionTypeBackground)
    fun setTransactionTypeBackground(view: ImageView, type: String?) {
        type?.let {
            val color = if (it == view.context.getString(R.string.expens_e)) R.drawable.red else R.drawable.greencir
            view.background = ContextCompat.getDrawable(view.context, color)
        }
    }

    @JvmStatic
    @BindingAdapter(transactionBackground)
    fun setTransactionBackground(layout: ConstraintLayout, transaction: TransactionResponse?) {
        transaction?.let {
            val context = layout.context
            val backgroundRes = if (transaction.type ==context.getString(R.string.expens_e)) {
                R.drawable.border_background
            } else {
                R.drawable.lightt_greyback
            }
            layout.setBackgroundResource(backgroundRes)
        }
    }
}
