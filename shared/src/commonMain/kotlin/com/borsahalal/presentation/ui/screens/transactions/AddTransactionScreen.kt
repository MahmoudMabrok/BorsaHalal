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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.borsahalal.data.database.entities.Stock
import com.borsahalal.data.database.entities.TransactionType
import com.borsahalal.presentation.ui.components.DatePickerField
import com.borsahalal.presentation.ui.components.TransactionTypeSelector
import com.borsahalal.presentation.viewmodels.StockViewModel
import com.borsahalal.presentation.viewmodels.TransactionViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    stockViewModel: StockViewModel = koinViewModel(),
    transactionViewModel: TransactionViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val stocks by stockViewModel.stocks.collectAsState()
    val isLoading by transactionViewModel.isLoading.collectAsState()
    val error by transactionViewModel.error.collectAsState()

    var transactionType by remember { mutableStateOf(TransactionType.BUY) }
    var selectedStock by remember { mutableStateOf<Stock?>(null) }
    var quantity by remember { mutableStateOf("") }
    var pricePerUnit by remember { mutableStateOf("") }
    var commission by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(System.currentTimeMillis()) }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            transactionViewModel.clearError()
        }
    }

    Scaffold(
        topAppBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Transaction Type Selector
            TransactionTypeSelector(
                selectedType = transactionType,
                onTypeSelected = { transactionType = it }
            )

            // Stock Selector
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedStock?.let { "${it.prefix} - ${it.name}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Stock") },
                    placeholder = { Text("Select a stock") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    stocks.forEach { stock ->
                        DropdownMenuItem(
                            text = { Text("${stock.prefix} - ${stock.name}") },
                            onClick = {
                                selectedStock = stock
                                expanded = false
                            }
                        )
                    }
                }
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
                label = { Text("Commission (Optional)") },
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
            if (selectedStock != null && quantity.isNotBlank() && pricePerUnit.isNotBlank()) {
                val qty = quantity.toDoubleOrNull() ?: 0.0
                val price = pricePerUnit.toDoubleOrNull() ?: 0.0
                val comm = commission.toDoubleOrNull() ?: 0.0
                val total = qty * price

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
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

            // Add Button
            Button(
                onClick = {
                    val stock = selectedStock
                    val qty = quantity.toDoubleOrNull()
                    val price = pricePerUnit.toDoubleOrNull()
                    val comm = commission.toDoubleOrNull() ?: 0.0

                    if (stock != null && qty != null && price != null && qty > 0 && price > 0) {
                        when (transactionType) {
                            TransactionType.BUY -> {
                                transactionViewModel.createBuyTransaction(
                                    stockId = stock.id,
                                    quantity = qty,
                                    pricePerUnit = price,
                                    commission = comm,
                                    date = date,
                                    notes = notes.takeIf { it.isNotBlank() }
                                )
                            }
                            TransactionType.SELL -> {
                                transactionViewModel.createSellTransaction(
                                    stockId = stock.id,
                                    quantity = qty,
                                    pricePerUnit = price,
                                    commission = comm,
                                    date = date,
                                    notes = notes.takeIf { it.isNotBlank() }
                                )
                            }
                        }
                        onNavigateBack()
                    }
                },
                enabled = selectedStock != null &&
                        quantity.toDoubleOrNull()?.let { it > 0 } == true &&
                        pricePerUnit.toDoubleOrNull()?.let { it > 0 } == true &&
                        !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isLoading) "Adding..." else "Add Transaction",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
