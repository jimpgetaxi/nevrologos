package com.jimpgetaxi.nevrologos.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserCompleteHistory(
    @Embedded val user: UserProfile,
    
    @Relation(
        parentColumn = "userId",
        entityColumn = "ownerId"
    )
    val medicalEvents: List<MedicalEvent>,
    
    @Relation(
        parentColumn = "userId",
        entityColumn = "ownerId"
    )
    val documents: List<DocumentRegistry>,
    
    @Relation(
        parentColumn = "userId",
        entityColumn = "ownerId"
    )
    val labs: List<BiometricLog>,
    
    @Relation(
        parentColumn = "userId",
        entityColumn = "ownerId"
    )
    val symptoms: List<SymptomEntry>
)
