package com.borsahalal.data.repository

import com.borsahalal.data.database.dao.SaleAllocationDao
import com.borsahalal.data.database.dao.StockDao
import com.borsahalal.data.database.dao.StockHoldingDao
import com.borsahalal.data.database.dao.TransactionDao
import com.borsahalal.data.database.entities.Stock
import kotlinx.coroutines.flow.first

class ReportRepository(
    private val stockDao: StockDao,
    private val transactionDao: TransactionDao,
    private val stockHoldingDao: StockHoldingDao,
    private val saleAllocationDao: SaleAllocationDao
) {
    suspend fun getTotalRealizedProfit(profileId: Long): Double =
        saleAllocationDao.getTotalProfitForProfile(profileId) ?: 0.0

    suspend fun getTotalUnrealizedProfit(profileId: Long, currentPrices: Map<Long, Double>): Double {
        val stocks = stockDao.getStocksByProfile(profileId).first()
        var totalUnrealizedProfit = 0.0

        for (stock in stocks) {
            val currentPrice = currentPrices[stock.id] ?: continue
            val quantity = stockHoldingDao.getTotalRemainingQuantity(stock.id) ?: 0.0
            val avgBuyPrice = stockHoldingDao.getAverageBuyPrice(stock.id) ?: 0.0

            totalUnrealizedProfit += (currentPrice - avgBuyPrice) * quantity
        }

        return totalUnrealizedProfit
    }

    suspend fun getPortfolioValue(profileId: Long, currentPrices: Map<Long, Double>): Double {
        val stocks = stockDao.getStocksByProfile(profileId).first()
        var totalValue = 0.0

        for (stock in stocks) {
            val currentPrice = currentPrices[stock.id] ?: continue
            val quantity = stockHoldingDao.getTotalRemainingQuantity(stock.id) ?: 0.0
            totalValue += currentPrice * quantity
        }

        return totalValue
    }

    suspend fun getTotalZakatDue(profileId: Long, currentPrices: Map<Long, Double>): Double {
        val stocks = stockDao.getStocksByProfile(profileId).first()
        var totalZakat = 0.0

        for (stock in stocks) {
            val currentPrice = currentPrices[stock.id] ?: continue
            val quantity = stockHoldingDao.getTotalRemainingQuantity(stock.id) ?: 0.0
            val stockValue = currentPrice * quantity
            totalZakat += stockValue * (stock.zakatPercentage / 100.0)
        }

        return totalZakat
    }

    suspend fun getStockPerformance(stockId: Long): StockPerformance {
        val totalBought = transactionDao.getTotalBuyQuantity(stockId) ?: 0.0
        val totalSold = transactionDao.getTotalSellQuantity(stockId) ?: 0.0
        val remaining = stockHoldingDao.getTotalRemainingQuantity(stockId) ?: 0.0
        val avgBuyPrice = stockHoldingDao.getAverageBuyPrice(stockId) ?: 0.0
        val realizedProfit = saleAllocationDao.getTotalProfitForStock(stockId) ?: 0.0

        return StockPerformance(
            totalBought = totalBought,
            totalSold = totalSold,
            remainingQuantity = remaining,
            averageBuyPrice = avgBuyPrice,
            realizedProfit = realizedProfit
        )
    }

    data class StockPerformance(
        val totalBought: Double,
        val totalSold: Double,
        val remainingQuantity: Double,
        val averageBuyPrice: Double,
        val realizedProfit: Double
    )
}
