package com.example.stoma_mobile.ui.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stoma_mobile.data.local.AppointmentEntity
import com.example.stoma_mobile.data.local.AppointmentStatus
import com.example.stoma_mobile.data.model.Doctor
import com.example.stoma_mobile.data.model.Service
import com.example.stoma_mobile.data.model.TimeSlot
import com.example.stoma_mobile.data.repository.AppointmentRepository
import com.example.stoma_mobile.data.repository.DoctorRepository
import com.example.stoma_mobile.data.repository.ScheduleRepository
import com.example.stoma_mobile.data.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class CalendarUiState(
    val doctor: Doctor? = null,
    val service: Service? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val slots: List<TimeSlot> = emptyList(),
    val selectedSlot: TimeSlot? = null,
    val pendingSlot: TimeSlot? = null,          // слот в bottom sheet
    val isLoadingDoctor: Boolean = true,
    val isLoadingSlots: Boolean = false,
    val error: String? = null,
    val confirmed: Boolean = false
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository,
    private val serviceRepository: ServiceRepository,
    private val scheduleRepository: ScheduleRepository,
    private val appointmentRepository: AppointmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val doctorId: String = savedStateHandle["doctorId"] ?: ""
    private val serviceId: String = savedStateHandle["serviceId"] ?: ""

    private val _state = MutableStateFlow(CalendarUiState())
    val state = _state.asStateFlow()

    init {
        loadDoctorAndService()
        selectDate(LocalDate.now())
    }

    fun selectDate(date: LocalDate) {
        _state.update { it.copy(selectedDate = date, selectedSlot = null) }
        loadSlots(date)
    }

    /** Пользователь тапнул слот — открываем bottom sheet с деталями. */
    fun openSlotDetails(slot: TimeSlot) {
        if (!slot.isAvailable) return
        _state.update { it.copy(pendingSlot = slot) }
    }

    fun dismissSlotDetails() {
        _state.update { it.copy(pendingSlot = null) }
    }

    /** Пользователь подтвердил в bottom sheet — запоминаем выбор. */
    fun confirmPendingSlot() {
        val slot = _state.value.pendingSlot ?: return
        _state.update { it.copy(selectedSlot = slot, pendingSlot = null) }
        // Сразу оформляем — на этом этапе без промежуточной кнопки.
        confirm()
    }

    fun confirm() {
        val slot = _state.value.selectedSlot ?: return
        val doctor = _state.value.doctor ?: return
        val service = _state.value.service ?: return

        viewModelScope.launch {
            appointmentRepository.create(
                AppointmentEntity(
                    doctorId = doctor.id,
                    doctorName = doctor.fullName,
                    doctorSpecialization = doctor.specialization,
                    serviceId = service.id,
                    serviceName = service.name,
                    servicePriceFrom = service.priceFrom,
                    dateTimeMillis = slot.dateTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                    status = AppointmentStatus.CONFIRMED
                )
            )
            _state.update { it.copy(confirmed = true) }
        }
    }

    fun resetConfirmation() {
        _state.update { it.copy(confirmed = false) }
    }

    fun retry() {
        loadDoctorAndService()
        loadSlots(_state.value.selectedDate)
    }

    private fun loadDoctorAndService() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingDoctor = true, error = null) }
            runCatching {
                val doctor = doctorRepository.getDoctorById(doctorId)
                val service = serviceRepository.getServiceById(serviceId)
                doctor to service
            }
                .onSuccess { (doctor, service) ->
                    _state.update {
                        it.copy(doctor = doctor, service = service, isLoadingDoctor = false)
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoadingDoctor = false,
                            error = e.message ?: "Ошибка загрузки"
                        )
                    }
                }
        }
    }

    private fun loadSlots(date: LocalDate) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingSlots = true) }
            runCatching { scheduleRepository.getSchedule(doctorId, date) }
                .onSuccess { schedule ->
                    _state.update {
                        it.copy(slots = schedule.slots, isLoadingSlots = false)
                    }
                }
                .onFailure {
                    _state.update { it.copy(slots = emptyList(), isLoadingSlots = false) }
                }
        }
    }
}