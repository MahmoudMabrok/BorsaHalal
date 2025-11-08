package com.borsahalal.data.repository

import com.borsahalal.data.database.dao.StockDao
import com.borsahalal.data.database.dao.StockHoldingDao
import com.borsahalal.data.database.entities.Stock
import com.borsahalal.data.database.entities.StockHolding
import kotlinx.coroutines.flow.Flow

class StockRepository(
    private val stockDao: StockDao,
    private val stockHoldingDao: StockHoldingDao
) {
    fun getStocksByProfile(profileId: Long): Flow<List<Stock>> =
        stockDao.getStocksByProfile(profileId)

    suspend fun getStockById(stockId: Long): Stock? =
        stockDao.getStockById(stockId)

    fun getStockByIdFlow(stockId: Long): Flow<Stock?> =
        stockDao.getStockByIdFlow(stockId)

    suspend fun getStockByPrefix(profileId: Long, prefix: String): Stock? =
        stockDao.getStockByPrefix(profileId, prefix)

    fun searchStocks(profileId: Long, query: String): Flow<List<Stock>> =
        stockDao.searchStocks(profileId, query)

    suspend fun createStock(
        profileId: Long,
        prefix: String,
        name: String,
        zakatPercentage: Double = 2.5,
        notes: String? = null
    ): Long {
        // Check if stock with this prefix already exists
        val existing = getStockByPrefix(profileId, prefix)
        if (existing != null) {
            throw IllegalArgumentException("Stock with prefix '$prefix' already exists")
        }

        val stock = Stock(
            profileId = profileId,
            prefix = prefix.uppercase(),
            name = name,
            zakatPercentage = zakatPercentage,
            notes = notes,
            createdAt = System.currentTimeMillis()
        )
        return stockDao.insertStock(stock)
    }

    suspend fun updateStock(stock: Stock) {
        stockDao.updateStock(stock)
    }

    suspend fun deleteStock(stock: Stock) {
        stockDao.deleteStock(stock)
    }

    suspend fun getStockCount(profileId: Long): Int =
        stockDao.getStockCount(profileId)

    // Stock holdings related methods
    suspend fun getActiveHoldings(stockId: Long): List<StockHolding> =
        stockHoldingDao.getActiveHoldings(stockId)

    fun getActiveHoldingsFlow(stockId: Long): Flow<List<StockHolding>> =
        stockHoldingDao.getActiveHoldingsFlow(stockId)

    suspend fun getTotalRemainingQuantity(stockId: Long): Double =
        stockHoldingDao.getTotalRemainingQuantity(stockId) ?: 0.0

    suspend fun getAverageBuyPrice(stockId: Long): Double =
        stockHoldingDao.getAverageBuyPrice(stockId) ?: 0.0

    suspend fun getCurrentValue(stockId: Long, currentPrice: Double): Double {
        val quantity = getTotalRemainingQuantity(stockId)
        return quantity * currentPrice
    }

    suspend fun getUnrealizedProfit(stockId: Long, currentPrice: Double): Double {
        val quantity = getTotalRemainingQuantity(stockId)
        val avgBuyPrice = getAverageBuyPrice(stockId)
        return (currentPrice - avgBuyPrice) * quantity
    }
}
