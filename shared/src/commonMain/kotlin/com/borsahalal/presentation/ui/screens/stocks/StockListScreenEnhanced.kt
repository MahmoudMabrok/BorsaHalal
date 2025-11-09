package com.borsahalal.presentation.ui.screens.stocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.borsahalal.data.database.entities.Stock
import com.borsahalal.presentation.ui.components.AddStockDialog
import com.borsahalal.presentation.ui.components.EditStockDialog
import com.borsahalal.presentation.ui.components.StockCard
import com.borsahalal.presentation.viewmodels.StockViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StockListScreenEnhanced(
    viewModel: StockViewModel = koinViewModel(),
    onStockClick: (Long) -> Unit = {}
) {
    val stocks by viewModel.stocks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedStock by remember { mutableStateOf<Stock?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Stock")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Stocks",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                stocks.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No stocks yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Tap + to add your first stock",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(stocks) { stock ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StockCard(
                                    stock = stock,
                                    // TODO: Load actual holdings and price data
                                    holdings = 0.0,
                                    averagePrice = 0.0,
                                    onClick = { onStockClick(stock.id) },
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = {
                                        selectedStock = stock
                                        showEditDialog = true
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Stock")
                                }

                                IconButton(
                                    onClick = {
                                        selectedStock = stock
                                        showDeleteDialog = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Stock",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialogs
        if (showAddDialog) {
            AddStockDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { prefix, name, zakatPercentage, notes ->
                    viewModel.createStock(prefix, name, zakatPercentage, notes)
                    showAddDialog = false
                }
            )
        }

        if (showEditDialog && selectedStock != null) {
            EditStockDialog(
                stock = selectedStock!!,
                onDismiss = {
                    showEditDialog = false
                    selectedStock = null
                },
                onConfirm = { name, zakatPercentage, notes ->
                    viewModel.updateStock(
                        selectedStock!!.copy(
                            name = name,
                            zakatPercentage = zakatPercentage,
                            notes = notes
                        )
                    )
                    showEditDialog = false
                    selectedStock = null
                }
            )
        }

        if (showDeleteDialog && selectedStock != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    selectedStock = null
                },
                title = { Text("Delete Stock?") },
                text = {
                    Text("Are you sure you want to delete '${selectedStock!!.name}'? This will delete all associated transactions.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteStock(selectedStock!!)
                            showDeleteDialog = false
                            selectedStock = null
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            selectedStock = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
