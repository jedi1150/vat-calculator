package com.sandello.ndscalculator

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Rate::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rateDao(): RateDao
}