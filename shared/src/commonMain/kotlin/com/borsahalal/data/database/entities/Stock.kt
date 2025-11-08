package com.borsahalal.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stocks",
    foreignKeys = [
        ForeignKey(
            entity = Profile::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("profileId")]
)
data class Stock(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val prefix: String, // e.g., "ALG", "SAR"
    val name: String,
    val zakatPercentage: Double = 2.5,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
