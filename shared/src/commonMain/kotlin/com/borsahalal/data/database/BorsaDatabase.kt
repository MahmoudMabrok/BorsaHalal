package com.borsahalal.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.borsahalal.data.database.converters.Converters
import com.borsahalal.data.database.dao.ProfileDao
import com.borsahalal.data.database.dao.SaleAllocationDao
import com.borsahalal.data.database.dao.StockDao
import com.borsahalal.data.database.dao.StockHoldingDao
import com.borsahalal.data.database.dao.TransactionDao
import com.borsahalal.data.database.entities.Profile
import com.borsahalal.data.database.entities.SaleAllocation
import com.borsahalal.data.database.entities.Stock
import com.borsahalal.data.database.entities.StockHolding
import com.borsahalal.data.database.entities.Transaction

@Database(
    entities = [
        Profile::class,
        Stock::class,
        Transaction::class,
        StockHolding::class,
        SaleAllocation::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class BorsaDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun stockDao(): StockDao
    abstract fun transactionDao(): TransactionDao
    abstract fun stockHoldingDao(): StockHoldingDao
    abstract fun saleAllocationDao(): SaleAllocationDao
}

// Database builder interface for platform-specific implementations
internal const val DATABASE_NAME = "borsa_halal.db"

expect fun getDatabaseBuilder(): RoomDatabase.Builder<BorsaDatabase>
