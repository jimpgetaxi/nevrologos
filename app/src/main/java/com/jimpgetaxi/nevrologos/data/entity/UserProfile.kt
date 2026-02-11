package com.jimpgetaxi.nevrologos.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val userId: String,
    val fullName: String,
    val diagnosis: String,
    val dob: Long,
    val dietType: String
)
