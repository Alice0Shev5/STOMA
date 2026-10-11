package com.example.stoma_mobile.data.repository

import com.example.stoma_mobile.data.local.AppointmentEntity
import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {
    fun observeAll(): Flow<List<AppointmentEntity>>
    suspend fun create(appointment: AppointmentEntity): Long
    suspend fun cancel(id: Long)
    suspend fun update(appointment: AppointmentEntity)
}