package com.borsahalal.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Stock::class,
            parentColumns = ["id"],
            childColumns = ["stockId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("stockId"), Index("date")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val stockId: Long,
    val type: TransactionType,
    val quantity: Double,
    val pricePerUnit: Double,
    val commission: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
    val notes: String? = null
)
