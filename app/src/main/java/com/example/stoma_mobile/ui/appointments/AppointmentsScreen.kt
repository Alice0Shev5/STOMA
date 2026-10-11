package com.example.stoma_mobile.ui.appointments

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stoma_mobile.data.local.AppointmentEntity
import com.example.stoma_mobile.data.local.AppointmentStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AppointmentsScreen(
    viewModel: AppointmentsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.upcoming.isEmpty() && state.past.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "У вас пока нет записей",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Запишитесь на приём на главном экране",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (state.upcoming.isNotEmpty()) {
            item {
                SectionHeader("Предстоящие приёмы")
            }
            items(state.upcoming, key = { it.id }) { appt ->
                AppointmentCard(
                    appointment = appt,
                    onCancel = { viewModel.cancel(appt.id) }
                )
            }
        }

        if (state.past.isNotEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
                SectionHeader("История")
            }
            items(state.past, key = { it.id }) { appt ->
                AppointmentCard(appointment = appt, onCancel = null)
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun AppointmentCard(
    appointment: AppointmentEntity,
    onCancel: (() -> Unit)?
) {
    val ruLocale = Locale("ru")
    val dateTime = Instant.ofEpochMilli(appointment.dateTimeMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
    val dateText = dateTime.format(
        DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", ruLocale)
    )

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        appointment.serviceName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        appointment.doctorName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusChip(appointment.status)
            }

            Spacer(Modifier.height(12.dp))

            Text(
                dateText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            if (onCancel != null && appointment.status == AppointmentStatus.CONFIRMED) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Отменить запись")
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: AppointmentStatus) {
    val (label, color) = when (status) {
        AppointmentStatus.PENDING -> "Ожидает" to Color(0xFFFB8C00)
        AppointmentStatus.CONFIRMED -> "Подтверждена" to Color(0xFF43A047)
        AppointmentStatus.COMPLETED -> "Завершена" to Color(0xFF757575)
        AppointmentStatus.CANCELLED -> "Отменена" to Color(0xFFE53935)
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}