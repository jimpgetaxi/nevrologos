package com.jimpgetaxi.nevrologos.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "biometric_logs",
    foreignKeys = [
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["userId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
data class BiometricLog(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val ownerId: String,
    val timestamp: Long,
    val paramName: String,
    val value: Double,
    val unit: String
)
