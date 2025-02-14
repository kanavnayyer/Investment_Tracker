package com.awesome.investmenttracker.model.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.awesome.investmenttracker.model.entities.CategoryTotal
import com.awesome.investmenttracker.model.entities.TransactionResponse


@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionResponse)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionResponse)

    @Query("SELECT * FROM transactions ORDER BY date ASC, time ASC")
    fun getAllTransactions(): LiveData<List<TransactionResponse>>

    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC, time ASC")
    fun getTransactionsByDateRange(
        startDate: Long,
        endDate: Long
    ): LiveData<List<TransactionResponse>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Income'")
    fun getTotalIncome(): LiveData<Double>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Expense'")
    fun getTotalExpense(): LiveData<Double>

    @Query(
        """
        SELECT ( 
            COALESCE((SELECT SUM(amount) FROM transactions WHERE type = 'Income'), 0) - 
            COALESCE((SELECT SUM(amount) FROM transactions WHERE type = 'Expense'), 0)
        ) 
    """
    )
    fun getTotalBalance(): LiveData<Double>

    @Query("SELECT * FROM transactions WHERE date >= strftime('%s', 'now', '-1 month') * 1000 ORDER BY date DESC, time DESC")
    fun getLastMonthTransactions(): LiveData<List<TransactionResponse>>

    @Query("SELECT category, SUM(amount) as total FROM transactions GROUP BY category")
    fun getCategoryTotals(): LiveData<List<CategoryTotal>>

    @Query("SELECT * FROM transactions WHERE category LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY date DESC")
    suspend fun searchTransactions(query: String): List<TransactionResponse>


}