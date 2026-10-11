package com.example.stoma_mobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AppointmentStatus {
    PENDING,      // ожидает подтверждения
    CONFIRMED,    // подтверждена
    COMPLETED,    // завершена
    CANCELLED     // отменена
}

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val doctorId: String,
    val doctorName: String,
    val doctorSpecialization: String,
    val serviceId: String,
    val serviceName: String,
    val servicePriceFrom: Int,
    val dateTimeMillis: Long,      // момент приёма
    val status: AppointmentStatus = AppointmentStatus.CONFIRMED,
    val createdAtMillis: Long = System.currentTimeMillis()
)