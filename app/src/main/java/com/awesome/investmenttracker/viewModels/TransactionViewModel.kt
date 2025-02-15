package com.awesome.investmenttracker.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.awesome.investmenttracker.R
import com.awesome.investmenttracker.R.string.shopping
import com.awesome.investmenttracker.model.Repository.TransactionRepository
import com.awesome.investmenttracker.model.database.InvestDb
import com.awesome.investmenttracker.model.entities.CategoryTotal
import com.awesome.investmenttracker.model.entities.TransactionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = getApplication<Application>()

    private val repository: TransactionRepository
    val categoryTotals: LiveData<List<CategoryTotal>>
    private val _currentDate = MutableLiveData<String>()
    val currentDate: LiveData<String> get() = _currentDate

    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> get() = _currentTime

    private val _amount = MutableLiveData<String>()
    val amount: LiveData<String> get() = _amount

    private val _description = MutableLiveData<String>()
    val description: MutableLiveData<String> get() = _description

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    private val _transactionType = MutableLiveData<String>()
    val transactionType: LiveData<String> get() = _transactionType

    private val _category = MutableLiveData<String>()
    val category: LiveData<String> get() = _category

    private val _searchResults = MediatorLiveData<List<TransactionResponse>>()

    fun setCategory(selectedCategory: String) {
        _category.value = selectedCategory
    }

    val categories = listOf(
        appContext.getString(R.string.salary),
        appContext.getString(R.string.business),
        appContext.getString(R.string.investment),
        appContext.getString(shopping),
        appContext.getString(R.string.Other),
        appContext.getString(R.string.transport_)
    )
    val allTransactions: LiveData<List<TransactionResponse>>
    val totalIncome: LiveData<Double> get() = repository.totalIncome
    val totalExpense: LiveData<Double>
    val totalBalance: LiveData<Double>
    val lastMonthTransactions: LiveData<List<TransactionResponse>>

    init {
        val transactionDao = InvestDb.getDatabase(application).transactionDao()
        repository = TransactionRepository(transactionDao)

        allTransactions = repository.allTransactions
        totalExpense = repository.totalExpense
        totalBalance = repository.totalBalance
        lastMonthTransactions = repository.lastMonthTransactions
        categoryTotals = repository.categoryTotals
        _currentDate.value = getCurrentDate()
        _currentTime.value = getCurrentTime()
    }

    fun setAmount(amount: String) {
        _amount.value = amount
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun setDate(date: String) {
        _selectedDate.value = date
    }

    fun setTransactionType(type: String) {
        _transactionType.value = type
    }

    fun saveTransaction() {
        val selectedDateString = _selectedDate.value ?: getCurrentDateFormatted()
        val selectedDateMillis = convertDateToMillis(selectedDateString)

        val newTransaction = TransactionResponse(
            amount = _amount.value?.toDoubleOrNull() ?: 0.0,
            category = _category.value ?: "",
            description = _description.value ?: "",
            date = selectedDateMillis,
            type = _transactionType.value ?: "",
            time = getCurrentTime()
        )

        insertTransaction(newTransaction)
    }

    private fun insertTransaction(transaction: TransactionResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTransaction(transaction)
        }
    }

    private var _filteredTransactions = MediatorLiveData<List<TransactionResponse>>()
    val filteredTransactions: LiveData<List<TransactionResponse>> get() = _filteredTransactions

    fun searchTransactions(query: String) {
        viewModelScope.launch {
            val filteredList = repository.searchTransactions(query)
            _filteredTransactions.postValue(filteredList)
        }
    }

    fun filterTransactionsByRange(startDate: Long, endDate: Long) {

        val source = repository.getTransactionsByDateRange(startDate, endDate)
        _filteredTransactions.addSource(source) { transactions ->
            _filteredTransactions.value = transactions
            _filteredTransactions.removeSource(source)
        }
    }


    fun loadTransactions(range: String) {

        val allFilter = appContext.resources.getString(R.string.all_)

        _filteredTransactions.removeSource(allTransactions)

        if (range == allFilter) {
            _filteredTransactions.addSource(allTransactions) { transactions ->
                _filteredTransactions.value = transactions
            }
        } else {
            val startOfRange = getStartOfRange(range)
            val endOfRange = getEndOfDay()
            filterTransactionsByRange(startOfRange, endOfRange)
        }
    }
    fun formatAmount(amount: Double?): String {
        return amount?.let {
            val formattedAmount = NumberFormat.getNumberInstance(Locale("en", "IN")).format(it)
            "₹ $formattedAmount"
        } ?: "₹ 0"
    }

    private fun getStartOfRange(range: String): Long {
        val calendar = Calendar.getInstance()
        val appContext = getApplication<Application>()
        when (range) {
            appContext.resources.getString(R.string.today_) -> calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            appContext.resources.getString(R.string.week_) -> calendar.apply {

                add(Calendar.DAY_OF_YEAR, -7)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            appContext.resources.getString(R.string.month_) -> calendar.apply {

                add(Calendar.DAY_OF_YEAR, -30)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            appContext.resources.getString(R.string.year_) -> calendar.apply {

                add(Calendar.DAY_OF_YEAR, -365)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }

        val time = calendar.timeInMillis

        return time
    }

    private fun getEndOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat(
            appContext.resources.getString(R.string.eeee_d_mmmm),
            Locale.getDefault()
        )
        return sdf.format(Date())
    }

    private fun getCurrentDateFormatted(): String {
        val sdf = SimpleDateFormat(
            appContext.resources.getString(R.string.yyyy_mm_dd),
            Locale.getDefault()
        )
        return sdf.format(Date())
    }

    private fun getCurrentTime(): String {
        val sdf =
            SimpleDateFormat(appContext.resources.getString(R.string.hh_mm_a), Locale.getDefault())
        return sdf.format(Date())
    }


    private fun convertDateToMillis(dateString: String): Long {
        val sdf = SimpleDateFormat(
            appContext.resources.getString(R.string.yyyy_MM_dd),
            Locale.getDefault()
        )
        return try {
            val date = sdf.parse(dateString)
            date?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {

            System.currentTimeMillis()
        }
    }
}
