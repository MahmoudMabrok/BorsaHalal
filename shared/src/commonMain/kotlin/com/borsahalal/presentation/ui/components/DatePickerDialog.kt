package com.borsahalal.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: Long,
    onValueChange: (Long) -> Unit,
    label: String = "Date",
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    val instant = Instant.fromEpochMilliseconds(value)
    val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    val formattedDate = "${localDate.dayOfMonth}/${localDate.monthNumber}/${localDate.year}"

    OutlinedTextField(
        value = formattedDate,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            Icon(
                Icons.Default.DateRange,
                contentDescription = "Select date",
                modifier = Modifier.clickable { showDialog = true }
            )
        },
        modifier = modifier.clickable { showDialog = true }
    )

    if (showDialog) {
        DatePickerDialog(
            initialDate = value,
            onDateSelected = { newDate ->
                onValueChange(newDate)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: Long = System.currentTimeMillis(),
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
