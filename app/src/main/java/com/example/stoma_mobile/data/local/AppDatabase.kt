package com.example.stoma_mobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AppointmentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appointmentDao(): AppointmentDao
}