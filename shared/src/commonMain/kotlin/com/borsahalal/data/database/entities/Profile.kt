package com.borsahalal.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class Profile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val currency: String = "USD",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = false
)
