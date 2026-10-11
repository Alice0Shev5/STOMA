package com.example.stoma_mobile.ui.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stoma_mobile.data.local.AppointmentEntity
import com.example.stoma_mobile.data.repository.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppointmentsUiState(
    val isLoading: Boolean = true,
    val upcoming: List<AppointmentEntity> = emptyList(),
    val past: List<AppointmentEntity> = emptyList()
)

@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val repository: AppointmentRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)

    val state: StateFlow<AppointmentsUiState> = repository.observeAll()
        .map { list ->
            val now = System.currentTimeMillis()
            val (upcoming, past) = list.partition { it.dateTimeMillis >= now }
            AppointmentsUiState(
                isLoading = false,
                upcoming = upcoming,
                past = past.sortedByDescending { it.dateTimeMillis }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppointmentsUiState()
        )

    fun cancel(id: Long) {
        viewModelScope.launch { repository.cancel(id) }
    }
}