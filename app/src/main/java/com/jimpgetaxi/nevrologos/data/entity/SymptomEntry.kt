package com.jimpgetaxi.nevrologos.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "symptom_entries",
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
data class SymptomEntry(
    @PrimaryKey(autoGenerate = true) val entryId: Long = 0,
    val ownerId: String,
    val timestamp: Long,
    val symptomType: String,
    val severity: Int
)
