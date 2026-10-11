package com.example.stoma_mobile.ui.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stoma_mobile.data.model.Service
import com.example.stoma_mobile.data.model.ServiceCategory
import com.example.stoma_mobile.data.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServiceUiState(
    val query: String = "",
    val selectedCategory: ServiceCategory? = null,
    val services: List<Service> = emptyList(),
    val isLoading: Boolean = true
)

@OptIn(FlowPreview::class)
@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val repository: ServiceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceUiState())
    val state = _state.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    private val categoryFlow = MutableStateFlow<ServiceCategory?>(null)

    init {
        // Реагируем на ввод и смену категории: debounce + загрузка
        viewModelScope.launch {
            combine(
                queryFlow.debounce(300),
                categoryFlow
            ) { q: String, c: ServiceCategory? ->
                q to c
            }
                .distinctUntilChanged()
                .collect { (q, c) ->
                    load(q, c)
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
        queryFlow.value = newQuery
    }

    fun onCategorySelected(category: ServiceCategory?) {
        _state.update { it.copy(selectedCategory = category) }
        categoryFlow.value = category
    }

    private suspend fun load(query: String, category: ServiceCategory?) {
        _state.update { it.copy(isLoading = true) }
        val result = repository.getServices(query, category)
        _state.update { it.copy(services = result, isLoading = false) }
    }
}