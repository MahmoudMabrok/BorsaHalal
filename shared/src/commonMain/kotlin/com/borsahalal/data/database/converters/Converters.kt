package com.borsahalal.data.database.converters

import androidx.room.TypeConverter
import com.borsahalal.data.database.entities.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
}
