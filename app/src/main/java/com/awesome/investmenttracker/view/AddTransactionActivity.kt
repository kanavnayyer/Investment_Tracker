package com.awesome.investmenttracker.view

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.awesome.investmenttracker.R
import com.awesome.investmenttracker.databinding.ActivityAddBinding

import com.awesome.investmenttracker.viewModels.TransactionViewModel

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private val viewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTransactionTypeSelection()
        setupCategoryDropdown()
        setupInputListeners()
        setupDatePicker()
        setupContinueButton()
    }

    private fun setupTransactionTypeSelection() {
        binding.chipIncome.setOnClickListener { viewModel.setTransactionType(getString(R.string.income_)) }
        binding.chipExpense.setOnClickListener { viewModel.setTransactionType(getString(R.string.expense_)) }
    }

    private fun setupCategoryDropdown() {
        val categoryAdapter = object : ArrayAdapter<String>(
            this, R.layout.custom_dropdown_item, viewModel.categories
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                if (viewModel.category.value == getItem(position)) {
                    view.setBackgroundResource(R.drawable.selected_item_border)
                } else {
                    view.setBackgroundResource(R.drawable.default_item_background)
                }
                return view
            }
        }

        binding.categoryDropdown.setAdapter(categoryAdapter)

        binding.categoryDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = viewModel.categories[position]
            binding.categoryDropdown.setText(selectedCategory, false)
            viewModel.setCategory(selectedCategory)
            categoryAdapter.notifyDataSetChanged()
        }

        binding.categoryDropdown.setOnClickListener {
            binding.categoryDropdown.showDropDown()
        }
    }


    private fun setupInputListeners() {
        binding.descriptionInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setDescription(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.amountInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setAmount(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupDatePicker() {
        binding.tvDate.setOnClickListener {
            val datePicker = DatePickerFragment { selectedDate ->
                binding.tvDate.text = SpannableStringBuilder(selectedDate)
                viewModel.setDate(selectedDate)
            }
            datePicker.show(supportFragmentManager, getString(R.string.datepicker))
        }
    }

    private fun setupContinueButton() {
        binding.continueButton.setOnClickListener {
            viewModel.setAmount(binding.amountInput.text.toString().trim())
            viewModel.setDescription(binding.descriptionInput.text.toString().trim())

            if (!areFieldsValid()) {
                Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (binding.amountInput.text.toString().trim()[0].equals('0')) {
                Toast.makeText(this, getString(R.string.dont_start_from_zero), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener

            }
            viewModel.saveTransaction()
            Toast.makeText(this, getString(R.string.transaction_added), Toast.LENGTH_SHORT).show()
            clearFields()
        }
    }

    private fun clearFields() {
        binding.amountInput.text.clear()
        binding.descriptionInput.text.clear()
        binding.categoryDropdown.setText("", false)
        binding.tvDate.text = SpannableStringBuilder("")
        binding.chipIncome.isChecked = false
        binding.chipExpense.isChecked = false

    }

    private fun areFieldsValid(): Boolean {
        return !viewModel.amount.value.isNullOrEmpty() &&
                !viewModel.category.value.isNullOrEmpty() &&
                !viewModel.transactionType.value.isNullOrEmpty() &&
                !viewModel.selectedDate.value.isNullOrEmpty() &&
                !viewModel.description.value.isNullOrEmpty()
    }

    fun navigateHome(view: View) {
        finish()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass.name.startsWith(
                "android.webkit."
            )
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x = ev.rawX + view.getLeft() - scrcoords[0]
            val y = ev.rawY + view.getTop() - scrcoords[1]
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) (getSystemService(
                INPUT_METHOD_SERVICE
            ) as InputMethodManager).hideSoftInputFromWindow(
                (this.window.decorView.applicationWindowToken),
                0
            )
        }
        return super.dispatchTouchEvent(ev)
    }
}
