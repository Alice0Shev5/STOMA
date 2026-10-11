package com.example.stoma_mobile.data.model

import java.time.LocalDate
import java.time.LocalDateTime

data class TimeSlot(
    val id: String,
    val dateTime: LocalDateTime,
    val isAvailable: Boolean
)

data class DaySchedule(
    val date: LocalDate,
    val slots: List<TimeSlot>
)