package com.example.stoma_mobile.data.model

data class Doctor(
    val id: String,
    val fullName: String,
    val photoUrl: String,          // пока пустая строка — заполним в моках
    val specialization: String,
    val experienceYears: Int,
    val rating: Float,             // 4.9
    val serviceIds: List<String>   // какие услуги оказывает
)