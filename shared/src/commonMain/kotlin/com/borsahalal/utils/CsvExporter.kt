package com.borsahalal.utils

import com.borsahalal.data.database.entities.Stock
import com.borsahalal.data.database.entities.Transaction
import com.borsahalal.data.database.entities.StockHolding
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object CsvExporter {

    fun exportTransactionsToCsv(transactions: List<Transaction>, stockMap: Map<Long, Stock>): String {
        val header = "Date,Stock,Type,Quantity,Price,Commission,Total,Notes\n"
        val rows = transactions.joinToString("\n") { transaction ->
            val stock = stockMap[transaction.stockId]
            val instant = Instant.fromEpochMilliseconds(transaction.date)
            val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
            val formattedDate = "${localDate.year}-${localDate.monthNumber.toString().padStart(2, '0')}-${localDate.dayOfMonth.toString().padStart(2, '0')}"
            val total = transaction.quantity * transaction.pricePerUnit + transaction.commission

            listOf(
                formattedDate,
                stock?.prefix ?: "Unknown",
                transaction.type.name,
                String.format("%.2f", transaction.quantity),
                String.format("%.2f", transaction.pricePerUnit),
                String.format("%.2f", transaction.commission),
                String.format("%.2f", total),
                "\"${transaction.notes?.replace("\"", "\"\"") ?: ""}\""
            ).joinToString(",")
        }

        return header + rows
    }

    fun exportStocksToCsv(stocks: List<Stock>, holdingsMap: Map<Long, Double>, avgPriceMap: Map<Long, Double>): String {
        val header = "Prefix,Name,Current Holdings,Average Price,Zakat %,Notes\n"
        val rows = stocks.joinToString("\n") { stock ->
            val holdings = holdingsMap[stock.id] ?: 0.0
            val avgPrice = avgPriceMap[stock.id] ?: 0.0

            listOf(
                stock.prefix,
                "\"${stock.name}\"",
                String.format("%.2f", holdings),
                String.format("%.2f", avgPrice),
                String.format("%.2f", stock.zakatPercentage),
                "\"${stock.notes?.replace("\"", "\"\"") ?: ""}\""
            ).joinToString(",")
        }

        return header + rows
    }

    fun exportHoldingsToCsv(holdings: List<StockHolding>, stockMap: Map<Long, Stock>): String {
        val header = "Stock,Buy Date,Original Quantity,Remaining Quantity,Buy Price,Current Value\n"
        val rows = holdings.joinToString("\n") { holding ->
            val stock = stockMap[holding.stockId]
            val instant = Instant.fromEpochMilliseconds(holding.buyDate)
            val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
            val formattedDate = "${localDate.year}-${localDate.monthNumber.toString().padStart(2, '0')}-${localDate.dayOfMonth.toString().padStart(2, '0')}"
            val currentValue = holding.remainingQuantity * holding.buyPrice

            listOf(
                stock?.prefix ?: "Unknown",
                formattedDate,
                String.format("%.2f", holding.originalQuantity),
                String.format("%.2f", holding.remainingQuantity),
                String.format("%.2f", holding.buyPrice),
                String.format("%.2f", currentValue)
            ).joinToString(",")
        }

        return header + rows
    }

    fun exportPortfolioSummaryToCsv(
        totalInvested: Double,
        portfolioValue: Double,
        realizedProfit: Double,
        unrealizedProfit: Double,
        totalProfit: Double,
        returnPercentage: Double,
        stockCount: Int,
        zakatDue: Double,
        currency: String
    ): String {
        val header = "Metric,Value,Currency\n"
        val rows = listOf(
            listOf("Total Invested", String.format("%.2f", totalInvested), currency),
            listOf("Current Portfolio Value", String.format("%.2f", portfolioValue), currency),
            listOf("Realized Profit", String.format("%.2f", realizedProfit), currency),
            listOf("Unrealized Profit", String.format("%.2f", unrealizedProfit), currency),
            listOf("Total Profit/Loss", String.format("%.2f", totalProfit), currency),
            listOf("Return (ROI)", String.format("%.2f%%", returnPercentage), ""),
            listOf("Active Stocks", stockCount.toString(), ""),
            listOf("Annual Zakat Due", String.format("%.2f", zakatDue), currency)
        ).joinToString("\n") { it.joinToString(",") }

        return header + rows
    }
}
