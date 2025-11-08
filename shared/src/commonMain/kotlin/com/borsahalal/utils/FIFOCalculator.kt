package com.borsahalal.utils

import com.borsahalal.data.database.entities.SaleAllocation
import com.borsahalal.data.database.entities.StockHolding
import com.borsahalal.data.database.entities.Transaction
import kotlin.math.min

/**
 * FIFO (First-In-First-Out) profit calculator for stock transactions.
 * This class implements the FIFO method for calculating profits from stock sales.
 */
class FIFOCalculator {

    /**
     * Calculates profit from a sell transaction using FIFO method.
     *
     * @param sellTransaction The sell transaction to calculate profit for
     * @param holdings List of stock holdings sorted by purchase date (oldest first)
     * @return ProfitCalculation containing gross profit, net profit, and allocations
     */
    fun calculateProfit(
        sellTransaction: Transaction,
        holdings: List<StockHolding>
    ): ProfitCalculation {
        var remainingToSell = sellTransaction.quantity
        val allocations = mutableListOf<SaleAllocation>()
        var totalCost = 0.0

        // Sort holdings by date (FIFO - oldest first)
        val sortedHoldings = holdings.sortedBy { it.buyDate }

        for (holding in sortedHoldings) {
            if (remainingToSell <= 0) break

            // Determine how much to take from this holding
            val quantityFromThisHolding = min(
                remainingToSell,
                holding.remainingQuantity
            )

            if (quantityFromThisHolding > 0) {
                val cost = quantityFromThisHolding * holding.buyPrice
                totalCost += cost

                // Create allocation record
                allocations.add(
                    SaleAllocation(
                        sellTransactionId = sellTransaction.id,
                        buyTransactionId = holding.buyTransactionId,
                        quantity = quantityFromThisHolding,
                        buyPrice = holding.buyPrice,
                        sellPrice = sellTransaction.pricePerUnit,
                        profit = (sellTransaction.pricePerUnit - holding.buyPrice) * quantityFromThisHolding
                    )
                )

                remainingToSell -= quantityFromThisHolding
            }
        }

        // Calculate total revenue and profits
        val totalRevenue = sellTransaction.quantity * sellTransaction.pricePerUnit
        val grossProfit = totalRevenue - totalCost
        val netProfit = grossProfit - sellTransaction.commission

        return ProfitCalculation(
            grossProfit = grossProfit,
            netProfit = netProfit,
            totalCost = totalCost,
            totalRevenue = totalRevenue,
            commission = sellTransaction.commission,
            allocations = allocations
        )
    }

    /**
     * Validates if a sell transaction can be executed given current holdings.
     *
     * @param sellQuantity Quantity to sell
     * @param holdings Current stock holdings
     * @return ValidationResult indicating if the sale is valid
     */
    fun validateSell(
        sellQuantity: Double,
        holdings: List<StockHolding>
    ): ValidationResult {
        val availableQuantity = holdings.sumOf { it.remainingQuantity }

        return if (sellQuantity <= availableQuantity) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(
                "Insufficient holdings. Available: $availableQuantity, Trying to sell: $sellQuantity"
            )
        }
    }
}

/**
 * Data class representing the result of a FIFO profit calculation.
 */
data class ProfitCalculation(
    val grossProfit: Double,
    val netProfit: Double,
    val totalCost: Double,
    val totalRevenue: Double,
    val commission: Double,
    val allocations: List<SaleAllocation>
)

/**
 * Sealed class representing the result of validation.
 */
sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()

    fun isValid(): Boolean = this is Valid
    fun getErrorMessage(): String? = (this as? Invalid)?.message
}
