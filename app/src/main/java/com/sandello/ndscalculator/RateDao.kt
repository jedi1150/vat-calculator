package com.sandello.ndscalculator

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RateDao {
    @Query("SELECT * FROM rates")
    fun getAll(): List<Rate>

    @Query("SELECT * FROM rates WHERE rate LIKE :rate LIMIT 1")
    fun findByCountry(rate: String): Rate

    @Insert
    fun insertAll(vararg rates: Rate)

    @Insert
    fun insertAllCountries(rates: List<Rate>)

    @Delete
    fun delete(rate: Rate)

    @Query("DELETE FROM rates")
    fun deleteAll()
}