package com.borsahalal.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getDatabaseBuilder(): RoomDatabase.Builder<BorsaDatabase> {
    val appContext = BorsaDatabaseContext.applicationContext
        ?: throw IllegalStateException("Application context not initialized")

    val dbFile = appContext.getDatabasePath(DATABASE_NAME)
    return Room.databaseBuilder<BorsaDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

object BorsaDatabaseContext {
    var applicationContext: Context? = null
}
