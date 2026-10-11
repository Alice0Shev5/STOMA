package com.example.stoma_mobile.data.repository.mock

import javax.inject.Inject
import com.example.stoma_mobile.data.mock.MockData
import com.example.stoma_mobile.data.model.Doctor
import com.example.stoma_mobile.data.repository.DoctorRepository
import kotlinx.coroutines.delay

class MockDoctorRepository @Inject constructor(): DoctorRepository {

    override suspend fun getDoctorsForService(serviceId: String): List<Doctor> {
        delay(300)
        return MockData.doctors.filter { serviceId in it.serviceIds }
    }

    override suspend fun getDoctorById(id: String): Doctor? {
        delay(150)
        return MockData.doctors.firstOrNull { it.id == id }
    }
}