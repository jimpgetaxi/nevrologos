package com.jimpgetaxi.nevrologos.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jimpgetaxi.nevrologos.data.dao.NeurologyDao
import com.jimpgetaxi.nevrologos.data.entity.*

@Database(
    entities = [
        UserProfile::class,
        MedicalEvent::class,
        DocumentRegistry::class,
        BiometricLog::class,
        SymptomEntry::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun neurologyDao(): NeurologyDao
}
