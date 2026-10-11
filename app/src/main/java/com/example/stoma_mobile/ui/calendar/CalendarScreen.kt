package com.example.stoma_mobile.ui.calendar

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stoma_mobile.data.model.TimeSlot
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    onConfirmed: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val today = LocalDate.now()
    val ruLocale = Locale("ru")

    Column(Modifier.fillMaxSize()) {

        // Шапка: имя врача
        state.doctor?.let { doctor ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        doctor.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        doctor.specialization,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    state.service?.let { service ->
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "${service.name} · от ${formatPrice(service.priceFrom)} сум · ${service.durationMinutes} мин",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // Лента дат
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items((0..13).toList()) { offset ->
                val date = today.plusDays(offset.toLong())
                DateChip(
                    date = date,
                    isToday = offset == 0,
                    isSelected = date == state.selectedDate,
                    onClick = { viewModel.selectDate(date) }
                )
            }
        }

        // Заголовок выбранного дня + счётчик свободных
        val availableCount = state.slots.count { it.isAvailable }
        val headerDate = state.selectedDate.format(
            DateTimeFormatter.ofPattern("EEEE, d MMMM", ruLocale)
        ).replaceFirstChar { it.titlecase(ruLocale) }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = headerDate,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.width(8.dp))
            if (!state.isLoadingSlots && availableCount > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "$availableCount свободно",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Список секций
        Box(Modifier.weight(1f)) {
            when {
                state.isLoadingSlots -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                availableCount == 0 -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "На этот день нет свободного времени",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    val morning = state.slots.filter {
                        it.dateTime.toLocalTime() < LocalTime.of(12, 0)
                    }
                    val afternoon = state.slots.filter {
                        val t = it.dateTime.toLocalTime()
                        !t.isBefore(LocalTime.of(12, 0)) && t < LocalTime.of(16, 0)
                    }
                    val evening = state.slots.filter {
                        !it.dateTime.toLocalTime().isBefore(LocalTime.of(16, 0))
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (morning.any { it.isAvailable }) {
                            item {
                                TimeSection(
                                    title = "Утро",
                                    slots = morning,
                                    selectedId = state.selectedSlot?.id,
                                    onSlotClick = viewModel::openSlotDetails
                                )
                            }
                        }
                        if (afternoon.any { it.isAvailable }) {
                            item {
                                TimeSection(
                                    title = "День",
                                    slots = afternoon,
                                    selectedId = state.selectedSlot?.id,
                                    onSlotClick = viewModel::openSlotDetails                                )
                            }
                        }
                        if (evening.any { it.isAvailable }) {
                            item {
                                TimeSection(
                                    title = "Вечер",
                                    slots = evening,
                                    selectedId = state.selectedSlot?.id,
                                    onSlotClick = viewModel::openSlotDetails                                )
                            }
                        }
                    }
                }
            }
        }

        // Нижняя панель с выбором и кнопкой
        Surface(shadowElevation = 6.dp, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                val slotText = state.selectedSlot?.let {
                    "Выбрано: ${
                        it.dateTime.format(
                            DateTimeFormatter.ofPattern("d MMMM, HH:mm", Locale("ru"))
                        )
                    }"
                } ?: "Выберите время приёма"
                Text(
                    text = slotText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Bottom sheet с деталями слота
    val pendingSlot = state.pendingSlot
    if (pendingSlot != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissSlotDetails() },
            sheetState = sheetState
        ) {
            SlotDetailsSheet(
                slot = pendingSlot,
                doctorName = state.doctor?.fullName.orEmpty(),
                doctorSpecialization = state.doctor?.specialization.orEmpty(),
                serviceName = state.service?.name.orEmpty(),
                servicePrice = state.service?.priceFrom ?: 0,
                serviceDuration = state.service?.durationMinutes ?: 0,
                onConfirm = { viewModel.confirmPendingSlot() },
                onCancel = { viewModel.dismissSlotDetails() }
            )
        }
    }

    // Диалог успеха — как было
    if (state.confirmed) {
        AlertDialog(
            onDismissRequest = { viewModel.resetConfirmation() },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Запись оформлена") },
            text = {
                val slot = state.selectedSlot
                val doctor = state.doctor
                if (slot != null && doctor != null) {
                    Text(
                        "Вы записаны к ${doctor.fullName}\n" +
                                slot.dateTime.format(
                                    DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", ruLocale)
                                )
                    )
                } else {
                    Text("Запись успешно создана.")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetConfirmation()
                    onConfirmed()
                }) { Text("Готово") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.resetConfirmation() }) { Text("Остаться") }
            }
        )
    }
}

@Composable
private fun DateChip(
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (isSelected) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurfaceVariant
    val dayName = date.dayOfWeek
        .getDisplayName(TextStyle.SHORT, Locale("ru"))
        .replaceFirstChar { it.titlecase(Locale("ru")) }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = bg,
        modifier = Modifier.width(60.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isToday) "Сег" else dayName,
                style = MaterialTheme.typography.labelMedium,
                color = fg
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = fg
            )
            // Точка-индикатор (заглушка — считаем, что в каждый день есть слоты)
            Spacer(Modifier.height(4.dp))
            Box(
                Modifier
                    .size(4.dp)
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun TimeSection(
    title: String,
    slots: List<TimeSlot>,
    selectedId: String?,
    onSlotClick: (TimeSlot) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // Ручная «сетка» из строк по 4 элемента
        val rows = slots.chunked(4)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { slot ->
                        Box(Modifier.weight(1f)) {
                            SlotChip(
                                slot = slot,
                                isSelected = selectedId == slot.id,
                                onClick = { onSlotClick(slot) }
                            )
                        }
                    }
                    // Заглушки, чтобы последний ряд был выровнен
                    repeat(4 - row.size) {
                        Box(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotChip(
    slot: TimeSlot,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val time = slot.dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

    if (isSelected) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                Modifier.padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else if (slot.isAvailable) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                Modifier.padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        // Занятый слот — просто серая плашка без клика
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                Modifier.padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SlotDetailsSheet(
    slot: TimeSlot,
    doctorName: String,
    doctorSpecialization: String,
    serviceName: String,
    servicePrice: Int,
    serviceDuration: Int,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val ruLocale = Locale("ru")
    val dateText = slot.dateTime.format(
        DateTimeFormatter.ofPattern("EEEE, d MMMM", ruLocale)
    ).replaceFirstChar { it.titlecase(ruLocale) }
    val timeText = slot.dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        Text(
            "Подтверждение записи",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(16.dp))

        // Дата и время
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    dateText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    timeText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Врач
        DetailRow(label = "Врач", value = doctorName)
        if (doctorSpecialization.isNotEmpty()) {
            DetailRow(label = "Специализация", value = doctorSpecialization)
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))

        // Услуга
        DetailRow(label = "Услуга", value = serviceName)
        DetailRow(label = "Длительность", value = "$serviceDuration мин")
        DetailRow(label = "Стоимость", value = "от ${formatPrice(servicePrice)} сум")

        Spacer(Modifier.height(24.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Отмена")
            }
            Button(
                onClick = onConfirm,
                modifier = Modifier.weight(1f)
            ) {
                Text("Записаться")
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatPrice(value: Int): String {
    val s = value.toString()
    val sb = StringBuilder()
    for ((i, ch) in s.withIndex()) {
        if (i > 0 && (s.length - i) % 3 == 0) sb.append(' ')
        sb.append(ch)
    }
    return sb.toString()
}