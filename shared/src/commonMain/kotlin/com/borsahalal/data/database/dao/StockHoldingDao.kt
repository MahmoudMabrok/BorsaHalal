package com.borsahalal.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.borsahalal.data.database.entities.StockHolding
import kotlinx.coroutines.flow.Flow

@Dao
interface StockHoldingDao {
    @Query("SELECT * FROM stock_holdings WHERE stockId = :stockId AND remainingQuantity > 0 ORDER BY buyDate ASC")
    suspend fun getActiveHoldings(stockId: Long): List<StockHolding>

    @Query("SELECT * FROM stock_holdings WHERE stockId = :stockId AND remainingQuantity > 0 ORDER BY buyDate ASC")
    fun getActiveHoldingsFlow(stockId: Long): Flow<List<StockHolding>>

    @Query("SELECT * FROM stock_holdings WHERE stockId = :stockId ORDER BY buyDate ASC")
    fun getAllHoldings(stockId: Long): Flow<List<StockHolding>>

    @Query("SELECT * FROM stock_holdings WHERE id = :holdingId")
    suspend fun getHoldingById(holdingId: Long): StockHolding?

    @Query("SELECT * FROM stock_holdings WHERE buyTransactionId = :transactionId")
    suspend fun getHoldingByTransactionId(transactionId: Long): StockHolding?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolding(holding: StockHolding): Long

    @Update
    suspend fun updateHolding(holding: StockHolding)

    @Delete
    suspend fun deleteHolding(holding: StockHolding)

    @Query("DELETE FROM stock_holdings WHERE stockId = :stockId")
    suspend fun deleteAllHoldingsForStock(stockId: Long)

    @Query("DELETE FROM stock_holdings WHERE buyTransactionId = :transactionId")
    suspend fun deleteHoldingByTransactionId(transactionId: Long)

    @Query("SELECT SUM(remainingQuantity) FROM stock_holdings WHERE stockId = :stockId")
    suspend fun getTotalRemainingQuantity(stockId: Long): Double?

    @Query("""
        SELECT SUM(remainingQuantity * buyPrice) / NULLIF(SUM(remainingQuantity), 0)
        FROM stock_holdings
        WHERE stockId = :stockId AND remainingQuantity > 0
    """)
    suspend fun getAverageBuyPrice(stockId: Long): Double?
}
