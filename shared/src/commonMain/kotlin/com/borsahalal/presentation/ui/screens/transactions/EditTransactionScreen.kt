package com.borsahalal.presentation.ui.screens.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.borsahalal.data.database.entities.TransactionType
import com.borsahalal.presentation.ui.components.DatePickerField
import com.borsahalal.presentation.viewmodels.TransactionViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionScreen(
    transactionId: Long,
    viewModel: TransactionViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val transaction by viewModel.selectedTransaction.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var quantity by remember { mutableStateOf("") }
    var pricePerUnit by remember { mutableStateOf("") }
    var commission by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(System.currentTimeMillis()) }
    var notes by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Load transaction data
    LaunchedEffect(transactionId) {
        viewModel.selectTransaction(transactionId)
    }

    LaunchedEffect(transaction) {
        transaction?.let {
            quantity = it.quantity.toString()
            pricePerUnit = it.pricePerUnit.toString()
            commission = it.commission.toString()
            date = it.date
            notes = it.notes ?: ""
        }
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topAppBar = {
            TopAppBar(
                title = { Text("Edit Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (transaction == null && isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (transaction != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Transaction Type Display (read-only)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (transaction!!.type == TransactionType.BUY) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.tertiaryContainer
                        }
                    )
                ) {
                    Text(
                        text = "Type: ${transaction!!.type.name}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                // Quantity
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                // Price Per Unit
                OutlinedTextField(
                    value = pricePerUnit,
                    onValueChange = { pricePerUnit = it },
                    label = { Text("Price Per Unit") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                // Commission
                OutlinedTextField(
                    value = commission,
                    onValueChange = { commission = it },
                    label = { Text("Commission") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                // Date Picker
                DatePickerField(
                    value = date,
                    onValueChange = { date = it },
                    label = "Transaction Date",
                    modifier = Modifier.fillMaxWidth()
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    placeholder = { Text("Any additional information") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                // Summary Card
                val qty = quantity.toDoubleOrNull() ?: 0.0
                val price = pricePerUnit.toDoubleOrNull() ?: 0.0
                val comm = commission.toDoubleOrNull() ?: 0.0
                val total = qty * price

                if (qty > 0 && price > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Transaction Summary",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Total: ${String.format("%.2f", total)}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (comm > 0) {
                                Text(
                                    text = "Commission: ${String.format("%.2f", comm)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Net: ${String.format("%.2f", total + comm)}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }

                // Update Button
                Button(
                    onClick = {
                        val qty = quantity.toDoubleOrNull()
                        val price = pricePerUnit.toDoubleOrNull()
                        val comm = commission.toDoubleOrNull() ?: 0.0

                        if (qty != null && price != null && qty > 0 && price > 0) {
                            val updatedTransaction = transaction!!.copy(
                                quantity = qty,
                                pricePerUnit = price,
                                commission = comm,
                                date = date,
                                notes = notes.takeIf { it.isNotBlank() }
                            )
                            viewModel.updateTransaction(updatedTransaction)
                            onNavigateBack()
                        }
                    },
                    enabled = quantity.toDoubleOrNull()?.let { it > 0 } == true &&
                            pricePerUnit.toDoubleOrNull()?.let { it > 0 } == true &&
                            !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isLoading) "Updating..." else "Update Transaction",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(transactionId)
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
