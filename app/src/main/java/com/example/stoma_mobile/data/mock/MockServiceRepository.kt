package com.example.stoma_mobile.data.repository.mock

import javax.inject.Inject
import com.example.stoma_mobile.data.mock.MockData
import com.example.stoma_mobile.data.model.Service
import com.example.stoma_mobile.data.model.ServiceCategory
import com.example.stoma_mobile.data.repository.ServiceRepository
import kotlinx.coroutines.delay

class MockServiceRepository @Inject constructor() : ServiceRepository {

    override suspend fun getServices(query: String, category: ServiceCategory?): List<Service> {
        delay(300) // имитация сети
        return MockData.services.filter { service ->
            val matchesQuery = query.isBlank() ||
                    service.name.contains(query, ignoreCase = true) ||
                    service.description.contains(query, ignoreCase = true)
            val matchesCategory = category == null || service.category == category
            matchesQuery && matchesCategory
        }
    }

    override suspend fun getServiceById(id: String): Service? {
        delay(150)
        return MockData.services.firstOrNull { it.id == id }
    }
}