package com.example.stoma_mobile.data.repository.impl

import com.example.stoma_mobile.data.local.AppointmentDao
import com.example.stoma_mobile.data.local.AppointmentEntity
import com.example.stoma_mobile.data.local.AppointmentStatus
import com.example.stoma_mobile.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppointmentRepositoryImpl @Inject constructor(
    private val dao: AppointmentDao
) : AppointmentRepository {

    override fun observeAll(): Flow<List<AppointmentEntity>> = dao.observeAll()

    override suspend fun create(appointment: AppointmentEntity): Long =
        dao.insert(appointment)

    override suspend fun cancel(id: Long) {
        val existing = dao.getById(id) ?: return
        dao.update(existing.copy(status = AppointmentStatus.CANCELLED))
    }

    override suspend fun update(appointment: AppointmentEntity) = dao.update(appointment)
}