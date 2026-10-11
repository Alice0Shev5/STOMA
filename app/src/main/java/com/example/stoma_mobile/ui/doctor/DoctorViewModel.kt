package com.example.stoma_mobile.ui.doctor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stoma_mobile.data.model.Doctor
import com.example.stoma_mobile.data.repository.DoctorRepository
import com.example.stoma_mobile.data.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DoctorUiState(
    val serviceName: String = "",
    val doctors: List<Doctor> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class DoctorViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository,
    private val serviceRepository: ServiceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val serviceId: String = savedStateHandle["serviceId"] ?: ""

    private val _state = MutableStateFlow(DoctorUiState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Имя услуги — чтобы показать в шапке ("Врачи по услуге: Лечение кариеса")
            val service = serviceRepository.getServiceById(serviceId)

            val result = runCatching {
                doctorRepository.getDoctorsForService(serviceId)
            }

            result
                .onSuccess { doctors ->
                    _state.update {
                        it.copy(
                            serviceName = service?.name.orEmpty(),
                            doctors = doctors,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Не удалось загрузить врачей"
                        )
                    }
                }
        }
    }
}