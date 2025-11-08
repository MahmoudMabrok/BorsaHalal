package com.borsahalal.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_holdings",
    foreignKeys = [
        ForeignKey(
            entity = Stock::class,
            parentColumns = ["id"],
            childColumns = ["stockId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Transaction::class,
            parentColumns = ["id"],
            childColumns = ["buyTransactionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("stockId"), Index("buyTransactionId")]
)
data class StockHolding(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val stockId: Long,
    val buyTransactionId: Long,
    val originalQuantity: Double,
    val remainingQuantity: Double,
    val buyPrice: Double,
    val buyDate: Long
)
