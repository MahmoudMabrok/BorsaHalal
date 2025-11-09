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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddStockDialog(
    onDismiss: () -> Unit,
    onConfirm: (prefix: String, name: String, zakatPercentage: Double, notes: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var prefix by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var zakatPercentage by remember { mutableStateOf("2.5") }
    var notes by remember { mutableStateOf("") }

    var prefixError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var zakatError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Stock",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = prefix,
                    onValueChange = {
                        prefix = it.uppercase()
                        prefixError = when {
                            it.isBlank() -> "Prefix cannot be empty"
                            it.length > 10 -> "Prefix too long"
                            else -> null
                        }
                    },
                    label = { Text("Stock Prefix") },
                    placeholder = { Text("e.g., AAPL, TSLA") },
                    isError = prefixError != null,
                    supportingText = prefixError?.let { { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = if (it.isBlank()) "Name cannot be empty" else null
                    },
                    label = { Text("Stock Name") },
                    placeholder = { Text("e.g., Apple Inc.") },
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
                    placeholder = { Text("2.5") },
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
                    placeholder = { Text("Any additional information") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val zakat = zakatPercentage.toDoubleOrNull() ?: 2.5
                    if (prefix.isNotBlank() && name.isNotBlank()) {
                        onConfirm(
                            prefix.trim().uppercase(),
                            name.trim(),
                            zakat,
                            notes.takeIf { it.isNotBlank() }
                        )
                    }
                },
                enabled = prefix.isNotBlank() && name.isNotBlank() && zakatError == null
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
