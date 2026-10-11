package com.example.stoma_mobile.di

import android.content.Context
import androidx.room.Room
import com.example.stoma_mobile.data.local.AppDatabase
import com.example.stoma_mobile.data.local.AppointmentDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "stoma_mobile.db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideAppointmentDao(db: AppDatabase): AppointmentDao = db.appointmentDao()
}