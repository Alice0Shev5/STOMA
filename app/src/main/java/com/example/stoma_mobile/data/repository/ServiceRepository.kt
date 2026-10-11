package com.example.stoma_mobile.data.repository

import com.example.stoma_mobile.data.model.Service
import com.example.stoma_mobile.data.model.ServiceCategory

interface ServiceRepository {
    suspend fun getServices(query: String = "", category: ServiceCategory? = null): List<Service>
    suspend fun getServiceById(id: String): Service?
}