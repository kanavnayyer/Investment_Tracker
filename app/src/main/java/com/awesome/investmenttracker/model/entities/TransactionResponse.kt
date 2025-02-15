package com.awesome.investmenttracker.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "transactions")
data class TransactionResponse(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val date: Long,
    val time: String,
    val category: String,
    val description: String,
    val amount: Double
)