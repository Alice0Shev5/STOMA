package com.example.stoma_mobile.data.repository

import com.example.stoma_mobile.data.model.Doctor

interface DoctorRepository {
    suspend fun getDoctorsForService(serviceId: String): List<Doctor>
    suspend fun getDoctorById(id: String): Doctor?
}