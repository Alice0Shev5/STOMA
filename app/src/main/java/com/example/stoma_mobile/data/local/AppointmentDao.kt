package com.example.stoma_mobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    @Query("SELECT * FROM appointments ORDER BY dateTimeMillis ASC")
    fun observeAll(): Flow<List<AppointmentEntity>>

    @Insert
    suspend fun insert(appointment: AppointmentEntity): Long

    @Update
    suspend fun update(appointment: AppointmentEntity)

    @Query("SELECT * FROM appointments WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): AppointmentEntity?

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteById(id: Long)
}