package com.jimpgetaxi.nevrologos.data.dao

import androidx.room.*
import com.jimpgetaxi.nevrologos.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NeurologyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Query("SELECT * FROM user_profiles")
    fun getAllProfiles(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    suspend fun getProfileById(userId: String): UserProfile?

    @Transaction
    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    fun getUserCompleteHistory(userId: String): Flow<UserCompleteHistory?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalEvent(event: MedicalEvent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentRegistry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBiometricLog(log: BiometricLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptomEntry(entry: SymptomEntry)

    @Delete
    suspend fun deleteProfile(profile: UserProfile)
}
