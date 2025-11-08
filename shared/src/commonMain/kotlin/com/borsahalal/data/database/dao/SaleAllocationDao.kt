package com.borsahalal.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.borsahalal.data.database.entities.SaleAllocation
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleAllocationDao {
    @Query("SELECT * FROM sale_allocations WHERE sellTransactionId = :sellTransactionId")
    suspend fun getAllocationsBySellTransaction(sellTransactionId: Long): List<SaleAllocation>

    @Query("SELECT * FROM sale_allocations WHERE sellTransactionId = :sellTransactionId")
    fun getAllocationsBySellTransactionFlow(sellTransactionId: Long): Flow<List<SaleAllocation>>

    @Query("SELECT * FROM sale_allocations WHERE buyTransactionId = :buyTransactionId")
    suspend fun getAllocationsByBuyTransaction(buyTransactionId: Long): List<SaleAllocation>

    @Query("""
        SELECT sa.* FROM sale_allocations sa
        INNER JOIN transactions t ON sa.sellTransactionId = t.id
        WHERE t.stockId = :stockId
        ORDER BY sa.id DESC
    """)
    fun getAllocationsByStock(stockId: Long): Flow<List<SaleAllocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllocation(allocation: SaleAllocation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllocations(allocations: List<SaleAllocation>)

    @Update
    suspend fun updateAllocation(allocation: SaleAllocation)

    @Delete
    suspend fun deleteAllocation(allocation: SaleAllocation)

    @Query("DELETE FROM sale_allocations WHERE sellTransactionId = :sellTransactionId")
    suspend fun deleteAllocationsBySellTransaction(sellTransactionId: Long)

    @Query("DELETE FROM sale_allocations WHERE buyTransactionId = :buyTransactionId")
    suspend fun deleteAllocationsByBuyTransaction(buyTransactionId: Long)

    @Query("""
        SELECT SUM(profit) FROM sale_allocations sa
        INNER JOIN transactions t ON sa.sellTransactionId = t.id
        WHERE t.stockId = :stockId
    """)
    suspend fun getTotalProfitForStock(stockId: Long): Double?

    @Query("""
        SELECT SUM(profit) FROM sale_allocations sa
        INNER JOIN transactions t ON sa.sellTransactionId = t.id
        INNER JOIN stocks s ON t.stockId = s.id
        WHERE s.profileId = :profileId
    """)
    suspend fun getTotalProfitForProfile(profileId: Long): Double?
}
