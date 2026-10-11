package com.example.stoma_mobile.data.repository

import com.example.stoma_mobile.data.model.DaySchedule
import com.example.stoma_mobile.data.model.TimeSlot
import java.time.LocalDate

interface ScheduleRepository {
    suspend fun getSchedule(doctorId: String, date: LocalDate): DaySchedule
    suspend fun getAvailableSlots(doctorId: String, from: LocalDate, days: Int): List<DaySchedule>
}