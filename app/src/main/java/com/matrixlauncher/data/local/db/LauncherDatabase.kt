package com.matrixlauncher.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AppCustomizationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LauncherDatabase : RoomDatabase() {
    abstract fun appCustomizationDao(): AppCustomizationDao

    companion object {
        const val DATABASE_NAME = "matrix_launcher.db"
    }
}
