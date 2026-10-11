package com.example.stoma_mobile.data.mock

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.stoma_mobile.data.model.Doctor
import com.example.stoma_mobile.data.model.Service
import com.example.stoma_mobile.data.model.ServiceCategory
import com.example.stoma_mobile.data.model.TimeSlot
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object MockData {

    val services: List<Service> = listOf(
        Service(
            id = "s1",
            name = "Консультация стоматолога",
            category = ServiceCategory.TREATMENT,
            description = "Осмотр, постановка диагноза, составление плана лечения",
            priceFrom = 50_000,
            durationMinutes = 30
        ),
        Service(
            id = "s2",
            name = "Лечение кариеса",
            category = ServiceCategory.TREATMENT,
            description = "Пломбирование зуба, лечение среднего и глубокого кариеса",
            priceFrom = 250_000,
            durationMinutes = 60
        ),
        Service(
            id = "s3",
            name = "Профессиональная чистка",
            category = ServiceCategory.HYGIENE,
            description = "Ультразвуковая чистка + полировка + фторирование",
            priceFrom = 350_000,
            durationMinutes = 60
        ),
        Service(
            id = "s4",
            name = "Имплантация зуба (1 этап)",
            category = ServiceCategory.IMPLANTATION,
            description = "Установка импланта, включая операцию и наблюдение",
            priceFrom = 3_500_000,
            durationMinutes = 120
        ),
        Service(
            id = "s5",
            name = "Установка брекетов",
            category = ServiceCategory.ORTHODONTICS,
            description = "Фиксация брекет-системы на одну челюсть",
            priceFrom = 4_000_000,
            durationMinutes = 90
        ),
        Service(
            id = "s6",
            name = "Металлокерамическая коронка",
            category = ServiceCategory.PROSTHETICS,
            description = "Изготовление и установка коронки из металлокерамики",
            priceFrom = 1_200_000,
            durationMinutes = 60
        ),
        Service(
            id = "s7",
            name = "Удаление зуба",
            category = ServiceCategory.TREATMENT,
            description = "Простое и сложное удаление, включая анестезию",
            priceFrom = 200_000,
            durationMinutes = 40
        ),
        Service(
            id = "s8",
            name = "Отбеливание Zoom",
            category = ServiceCategory.HYGIENE,
            description = "Кабинетное отбеливание системы Zoom 4",
            priceFrom = 2_500_000,
            durationMinutes = 90
        )
    )

    val doctors: List<Doctor> = listOf(
        Doctor(
            id = "d1",
            fullName = "Азизов Рустам Бахтиёрович",
            photoUrl = "",
            specialization = "Терапевт-стоматолог",
            experienceYears = 12,
            rating = 4.9f,
            serviceIds = listOf("s1", "s2", "s7")
        ),
        Doctor(
            id = "d2",
            fullName = "Каримова Дилноза Ахмедовна",
            photoUrl = "",
            specialization = "Гигиенист, пародонтолог",
            experienceYears = 8,
            rating = 4.8f,
            serviceIds = listOf("s1", "s3", "s8")
        ),
        Doctor(
            id = "d3",
            fullName = "Юсупов Шерзод Икромович",
            photoUrl = "",
            specialization = "Имплантолог, хирург",
            experienceYears = 15,
            rating = 5.0f,
            serviceIds = listOf("s4", "s7")
        ),
        Doctor(
            id = "d4",
            fullName = "Насирова Гулноза Фархадовна",
            photoUrl = "",
            specialization = "Ортодонт",
            experienceYears = 10,
            rating = 4.9f,
            serviceIds = listOf("s5")
        ),
        Doctor(
            id = "d5",
            fullName = "Ташкенбаев Алишер Улугбекович",
            photoUrl = "",
            specialization = "Ортопед",
            experienceYears = 14,
            rating = 4.8f,
            serviceIds = listOf("s6")
        )
    )

    /**
     * Генерирует слоты на указанную дату.
     * Рабочие часы: 9:00–19:00, шаг 30 минут.
     * Часть слотов помечаем как занятые — детерминированно по хешу времени,
     * чтобы при повторном открытии картина не «прыгала».
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleFor(doctorId: String, date: LocalDate): List<TimeSlot> {
        val slots = mutableListOf<TimeSlot>()
        var time = LocalTime.of(9, 0)
        val end = LocalTime.of(19, 0)
        var index = 0

        while (time.isBefore(end)) {
            val dateTime = LocalDateTime.of(date, time)
            val seed = (dateTime.dayOfYear * 31 + dateTime.hour * 60 + dateTime.minute + doctorId.hashCode())
            val busy = seed % 5 == 0 || seed % 7 == 0

            slots += TimeSlot(
                id = "${doctorId}_${date}_${time}",
                dateTime = dateTime,
                isAvailable = !busy
            )
            time = time.plusMinutes(30)
            index++
        }
        return slots
    }
}