package com.example.stoma_mobile.data.model

enum class ServiceCategory(val displayName: String) {
    TREATMENT("Лечение"),
    HYGIENE("Гигиена"),
    IMPLANTATION("Имплантация"),
    ORTHODONTICS("Ортодонтия"),
    PROSTHETICS("Протезирование")
}

data class Service(
    val id: String,
    val name: String,
    val category: ServiceCategory,
    val description: String,
    val priceFrom: Int,           // в сумах
    val durationMinutes: Int
)