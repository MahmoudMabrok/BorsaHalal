package com.borsahalal.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sale_allocations",
    foreignKeys = [
        ForeignKey(
            entity = Transaction::class,
            parentColumns = ["id"],
            childColumns = ["sellTransactionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Transaction::class,
            parentColumns = ["id"],
            childColumns = ["buyTransactionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sellTransactionId"), Index("buyTransactionId")]
)
data class SaleAllocation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sellTransactionId: Long,
    val buyTransactionId: Long,
    val quantity: Double,
    val buyPrice: Double,
    val sellPrice: Double,
    val profit: Double
)
