package com.borsahalal.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.borsahalal.data.database.entities.Transaction
import com.borsahalal.data.database.entities.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("""
        SELECT t.* FROM transactions t
        INNER JOIN stocks s ON t.stockId = s.id
        WHERE s.profileId = :profileId
        ORDER BY t.date DESC
    """)
    fun getTransactionsByProfile(profileId: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE stockId = :stockId ORDER BY date DESC")
    fun getTransactionsByStock(stockId: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: Long): Transaction?

    @Query("SELECT * FROM transactions WHERE stockId = :stockId AND type = :type ORDER BY date ASC")
    suspend fun getTransactionsByType(stockId: Long, type: TransactionType): List<Transaction>

    @Query("""
        SELECT * FROM transactions
        WHERE stockId = :stockId AND type = 'BUY' AND date <= :beforeDate
        ORDER BY date ASC
    """)
    suspend fun getBuyTransactionsBeforeDate(stockId: Long, beforeDate: Long): List<Transaction>

    @Query("""
        SELECT * FROM transactions
        WHERE stockId = :stockId AND date >= :startDate AND date <= :endDate
        ORDER BY date DESC
    """)
    fun getTransactionsByDateRange(stockId: Long, startDate: Long, endDate: Long): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE stockId = :stockId")
    suspend fun deleteAllTransactionsForStock(stockId: Long)

    @Query("SELECT SUM(quantity) FROM transactions WHERE stockId = :stockId AND type = 'BUY'")
    suspend fun getTotalBuyQuantity(stockId: Long): Double?

    @Query("SELECT SUM(quantity) FROM transactions WHERE stockId = :stockId AND type = 'SELL'")
    suspend fun getTotalSellQuantity(stockId: Long): Double?
}
