package com.awesome.investmenttracker.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.awesome.investmenttracker.R
import com.awesome.investmenttracker.model.entities.TransactionResponse
import com.awesome.investmenttracker.model.daos.TransactionDao


@Database(entities = [TransactionResponse::class], version = 3, exportSchema = false)
abstract class InvestDb: RoomDatabase() {
    abstract fun transactionDao (): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: InvestDb? = null

        fun getDatabase(context: Context): InvestDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InvestDb::class.java,
                    context.getString(R.string.invest_db)
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}


