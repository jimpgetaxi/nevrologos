package com.jimpgetaxi.nevrologos.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "document_registry",
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
data class DocumentRegistry(
    @PrimaryKey val docId: String,
    val ownerId: String,
    val uri: String,
    val mimeType: String,
    val contentHash: String,
    val aiAnalysisStatus: String
)
