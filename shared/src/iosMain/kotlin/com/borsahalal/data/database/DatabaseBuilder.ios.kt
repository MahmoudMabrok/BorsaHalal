package com.borsahalal.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual fun getDatabaseBuilder(): RoomDatabase.Builder<BorsaDatabase> {
    val dbFile = NSHomeDirectory() + "/$DATABASE_NAME"
    return Room.databaseBuilder<BorsaDatabase>(
        name = dbFile,
    )
}
