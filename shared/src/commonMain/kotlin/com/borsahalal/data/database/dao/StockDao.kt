package com.borsahalal.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.borsahalal.data.database.entities.Stock
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Query("SELECT * FROM stocks WHERE profileId = :profileId ORDER BY name ASC")
    fun getStocksByProfile(profileId: Long): Flow<List<Stock>>

    @Query("SELECT * FROM stocks WHERE id = :stockId")
    suspend fun getStockById(stockId: Long): Stock?

    @Query("SELECT * FROM stocks WHERE id = :stockId")
    fun getStockByIdFlow(stockId: Long): Flow<Stock?>

    @Query("SELECT * FROM stocks WHERE profileId = :profileId AND prefix = :prefix")
    suspend fun getStockByPrefix(profileId: Long, prefix: String): Stock?

    @Query("""
        SELECT * FROM stocks
        WHERE profileId = :profileId
        AND (name LIKE '%' || :query || '%' OR prefix LIKE '%' || :query || '%')
        ORDER BY name ASC
    """)
    fun searchStocks(profileId: Long, query: String): Flow<List<Stock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: Stock): Long

    @Update
    suspend fun updateStock(stock: Stock)

    @Delete
    suspend fun deleteStock(stock: Stock)

    @Query("SELECT COUNT(*) FROM stocks WHERE profileId = :profileId")
    suspend fun getStockCount(profileId: Long): Int
}
