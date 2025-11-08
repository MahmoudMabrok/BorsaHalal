package com.borsahalal.data.repository

import com.borsahalal.data.database.dao.SaleAllocationDao
import com.borsahalal.data.database.dao.StockHoldingDao
import com.borsahalal.data.database.dao.TransactionDao
import com.borsahalal.data.database.entities.SaleAllocation
import com.borsahalal.data.database.entities.StockHolding
import com.borsahalal.data.database.entities.Transaction
import com.borsahalal.data.database.entities.TransactionType
import com.borsahalal.utils.FIFOCalculator
import com.borsahalal.utils.ProfitCalculation
import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val stockHoldingDao: StockHoldingDao,
    private val saleAllocationDao: SaleAllocationDao,
    private val fifoCalculator: FIFOCalculator
) {
    fun getTransactionsByProfile(profileId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByProfile(profileId)

    fun getTransactionsByStock(stockId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByStock(stockId)

    suspend fun getTransactionById(transactionId: Long): Transaction? =
        transactionDao.getTransactionById(transactionId)

    fun getTransactionsByDateRange(
        stockId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<Transaction>> =
        transactionDao.getTransactionsByDateRange(stockId, startDate, endDate)

    suspend fun createBuyTransaction(
        stockId: Long,
        quantity: Double,
        pricePerUnit: Double,
        commission: Double = 0.0,
        date: Long = System.currentTimeMillis(),
        notes: String? = null
    ): Long {
        if (quantity <= 0) throw IllegalArgumentException("Quantity must be positive")
        if (pricePerUnit <= 0) throw IllegalArgumentException("Price must be positive")

        val transaction = Transaction(
            stockId = stockId,
            type = TransactionType.BUY,
            quantity = quantity,
            pricePerUnit = pricePerUnit,
            commission = commission,
            date = date,
            notes = notes
        )

        val transactionId = transactionDao.insertTransaction(transaction)

        // Create stock holding
        val holding = StockHolding(
            stockId = stockId,
            buyTransactionId = transactionId,
            originalQuantity = quantity,
            remainingQuantity = quantity,
            buyPrice = pricePerUnit,
            buyDate = date
        )
        stockHoldingDao.insertHolding(holding)

        return transactionId
    }

    suspend fun createSellTransaction(
        stockId: Long,
        quantity: Double,
        pricePerUnit: Double,
        commission: Double = 0.0,
        date: Long = System.currentTimeMillis(),
        notes: String? = null
    ): Long {
        if (quantity <= 0) throw IllegalArgumentException("Quantity must be positive")
        if (pricePerUnit <= 0) throw IllegalArgumentException("Price must be positive")

        // Check if enough holdings exist
        val availableQuantity = stockHoldingDao.getTotalRemainingQuantity(stockId) ?: 0.0
        if (quantity > availableQuantity) {
            throw IllegalArgumentException(
                "Insufficient holdings. Available: $availableQuantity, Trying to sell: $quantity"
            )
        }

        val transaction = Transaction(
            stockId = stockId,
            type = TransactionType.SELL,
            quantity = quantity,
            pricePerUnit = pricePerUnit,
            commission = commission,
            date = date,
            notes = notes
        )

        val transactionId = transactionDao.insertTransaction(transaction)

        // Calculate FIFO and create allocations
        val holdings = stockHoldingDao.getActiveHoldings(stockId)
        val profitCalculation = fifoCalculator.calculateProfit(
            sellTransaction = transaction.copy(id = transactionId),
            holdings = holdings
        )

        // Update holdings
        updateHoldingsAfterSale(profitCalculation)

        // Save allocations
        saleAllocationDao.insertAllocations(profitCalculation.allocations)

        return transactionId
    }

    private suspend fun updateHoldingsAfterSale(profitCalculation: ProfitCalculation) {
        for (allocation in profitCalculation.allocations) {
            val holding = stockHoldingDao.getHoldingByTransactionId(allocation.buyTransactionId)
            if (holding != null) {
                val updatedHolding = holding.copy(
                    remainingQuantity = holding.remainingQuantity - allocation.quantity
                )
                stockHoldingDao.updateHolding(updatedHolding)
            }
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {
        // For simplicity, we'll delete and recreate
        // In production, you'd want more sophisticated logic
        deleteTransaction(transaction.id)

        when (transaction.type) {
            TransactionType.BUY -> createBuyTransaction(
                stockId = transaction.stockId,
                quantity = transaction.quantity,
                pricePerUnit = transaction.pricePerUnit,
                commission = transaction.commission,
                date = transaction.date,
                notes = transaction.notes
            )
            TransactionType.SELL -> createSellTransaction(
                stockId = transaction.stockId,
                quantity = transaction.quantity,
                pricePerUnit = transaction.pricePerUnit,
                commission = transaction.commission,
                date = transaction.date,
                notes = transaction.notes
            )
        }
    }

    suspend fun deleteTransaction(transactionId: Long) {
        val transaction = getTransactionById(transactionId)
            ?: throw IllegalArgumentException("Transaction not found")

        when (transaction.type) {
            TransactionType.BUY -> {
                // Delete holding and any allocations
                stockHoldingDao.deleteHoldingByTransactionId(transactionId)
                saleAllocationDao.deleteAllocationsByBuyTransaction(transactionId)
            }
            TransactionType.SELL -> {
                // Restore holdings and delete allocations
                val allocations = saleAllocationDao.getAllocationsBySellTransaction(transactionId)
                for (allocation in allocations) {
                    val holding = stockHoldingDao.getHoldingByTransactionId(allocation.buyTransactionId)
                    if (holding != null) {
                        val updatedHolding = holding.copy(
                            remainingQuantity = holding.remainingQuantity + allocation.quantity
                        )
                        stockHoldingDao.updateHolding(updatedHolding)
                    }
                }
                saleAllocationDao.deleteAllocationsBySellTransaction(transactionId)
            }
        }

        transactionDao.deleteTransaction(transaction)
    }

    suspend fun getTotalBuyQuantity(stockId: Long): Double =
        transactionDao.getTotalBuyQuantity(stockId) ?: 0.0

    suspend fun getTotalSellQuantity(stockId: Long): Double =
        transactionDao.getTotalSellQuantity(stockId) ?: 0.0

    suspend fun getTotalProfitForStock(stockId: Long): Double =
        saleAllocationDao.getTotalProfitForStock(stockId) ?: 0.0
}
