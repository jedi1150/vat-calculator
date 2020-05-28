package com.sandello.ndscalculator

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RateDao {
    @Query("SELECT * FROM rates ORDER BY code, rate DESC")
    fun getAll(): List<Rate>

    @Query("SELECT * FROM rates WHERE code LIKE :code LIMIT 1")
    fun findByCountry(code: String): Rate?

    @Query("SELECT * FROM rates WHERE id = :id")
    fun findById(id: Int): Rate?

    @Insert
    fun insertAll(vararg rates: Rate)

    @Insert
    fun insertAllCountries(rates: List<Rate>)

    @Delete
    fun delete(rate: Rate)

    @Query("DELETE FROM rates")
    fun deleteAll()
}