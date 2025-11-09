package com.borsahalal.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.borsahalal.data.database.entities.Stock

@Composable
fun EditStockDialog(
    stock: Stock,
    onDismiss: () -> Unit,
    onConfirm: (name: String, zakatPercentage: Double, notes: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(stock.name) }
    var zakatPercentage by remember { mutableStateOf(stock.zakatPercentage.toString()) }
    var notes by remember { mutableStateOf(stock.notes ?: "") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var zakatError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Stock: ${stock.prefix}",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = if (it.isBlank()) "Name cannot be empty" else null
                    },
                    label = { Text("Stock Name") },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = zakatPercentage,
                    onValueChange = {
                        zakatPercentage = it
                        zakatError = try {
                            val value = it.toDoubleOrNull()
                            when {
                                value == null -> "Invalid number"
                                value < 0 -> "Cannot be negative"
                                value > 100 -> "Cannot exceed 100%"
                                else -> null
                            }
                        } catch (e: Exception) {
                            "Invalid number"
                        }
                    },
                    label = { Text("Zakat Percentage") },
                    isError = zakatError != null,
                    supportingText = zakatError?.let { { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val zakat = zakatPercentage.toDoubleOrNull() ?: stock.zakatPercentage
                    if (name.isNotBlank()) {
                        onConfirm(
                            name.trim(),
                            zakat,
                            notes.takeIf { it.isNotBlank() }
                        )
                    }
                },
                enabled = name.isNotBlank() && zakatError == null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
