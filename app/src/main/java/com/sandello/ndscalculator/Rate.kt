package com.sandello.ndscalculator

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "rates")
data class Rate(
        @PrimaryKey
        var id: Int = 0,
        var code: String = "",
        var rate: Float = 0f,
        var type: String = ""
)