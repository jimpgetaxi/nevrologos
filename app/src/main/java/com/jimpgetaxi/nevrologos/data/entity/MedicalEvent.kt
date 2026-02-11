package com.jimpgetaxi.nevrologos.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medical_events",
    foreignKeys = [
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["userId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"]), Index(value = ["date"])]
)
data class MedicalEvent(
    @PrimaryKey(autoGenerate = true) val eventId: Long = 0,
    val ownerId: String,
    val date: Long,
    val type: EventType,
    val clinicalNotes: String?,
    val aiSummary: String?
)

enum class EventType {
    RELAPSE, MRI, BLOOD_TEST, CHECKUP
}
