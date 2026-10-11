package com.example.stoma_mobile.data.repository.mock

import javax.inject.Inject
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.stoma_mobile.data.mock.MockData
import com.example.stoma_mobile.data.model.DaySchedule
import com.example.stoma_mobile.data.repository.ScheduleRepository
import kotlinx.coroutines.delay
import java.time.LocalDate

class MockScheduleRepository @Inject constructor() : ScheduleRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getSchedule(doctorId: String, date: LocalDate): DaySchedule {
        delay(250)
        return DaySchedule(
            date = date,
            slots = MockData.scheduleFor(doctorId, date)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAvailableSlots(
        doctorId: String,
        from: LocalDate,
        days: Int
    ): List<DaySchedule> {
        delay(300)
        return (0 until days).map { offset ->
            val date = from.plusDays(offset.toLong())
            DaySchedule(date = date, slots = MockData.scheduleFor(doctorId, date))
        }
    }
}