package com.awesome.investmenttracker.model.Repository

import androidx.lifecycle.LiveData
import com.awesome.investmenttracker.model.daos.TransactionDao
import com.awesome.investmenttracker.model.entities.CategoryTotal
import com.awesome.investmenttracker.model.entities.TransactionResponse


class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: LiveData<List<TransactionResponse>> = transactionDao.getAllTransactions()
    val totalIncome: LiveData<Double> = transactionDao.getTotalIncome()
    val totalExpense: LiveData<Double> = transactionDao.getTotalExpense()
    val totalBalance: LiveData<Double> = transactionDao.getTotalBalance()
    val lastMonthTransactions: LiveData<List<TransactionResponse>> = transactionDao.getLastMonthTransactions()

    val categoryTotals: LiveData<List<CategoryTotal>> = transactionDao.getCategoryTotals()

    suspend fun insertTransaction(transaction: TransactionResponse) {
        transactionDao.insertTransaction(transaction)
    }


    fun getTransactionsByDateRange(startDate: Long, endDate: Long): LiveData<List<TransactionResponse>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }



    suspend fun searchTransactions(query: String): List<TransactionResponse> {
        return transactionDao.searchTransactions(query)
    }



}
