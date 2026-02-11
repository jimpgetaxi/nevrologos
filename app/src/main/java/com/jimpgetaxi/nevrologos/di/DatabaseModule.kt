package com.jimpgetaxi.nevrologos.di

import android.content.Context
import androidx.room.Room
import com.jimpgetaxi.nevrologos.data.dao.NeurologyDao
import com.jimpgetaxi.nevrologos.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "neurology_db"
        ).build()
    }

    @Provides
    fun provideNeurologyDao(database: AppDatabase): NeurologyDao {
        return database.neurologyDao()
    }
}
