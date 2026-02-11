package com.jimpgetaxi.nevrologos.data.repository

import com.jimpgetaxi.nevrologos.data.dao.NeurologyDao
import com.jimpgetaxi.nevrologos.data.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NeurologyRepository @Inject constructor(
    private val dao: NeurologyDao
) {
    fun getAllProfiles(): Flow<List<UserProfile>> = dao.getAllProfiles()

    suspend fun insertProfile(profile: UserProfile) = dao.insertProfile(profile)

    suspend fun getProfileById(userId: String) = dao.getProfileById(userId)

    fun getUserCompleteHistory(userId: String): Flow<UserCompleteHistory?> = 
        dao.getUserCompleteHistory(userId)

    suspend fun insertMedicalEvent(event: MedicalEvent) = dao.insertMedicalEvent(event)
    
    suspend fun insertDocument(doc: DocumentRegistry) = dao.insertDocument(doc)
    
    suspend fun insertBiometricLog(log: BiometricLog) = dao.insertBiometricLog(log)
    
    suspend fun insertSymptomEntry(entry: SymptomEntry) = dao.insertSymptomEntry(entry)
}
