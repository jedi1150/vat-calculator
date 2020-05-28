package com.sandello.ndscalculator

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Rate::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rateDao(): RateDao
}