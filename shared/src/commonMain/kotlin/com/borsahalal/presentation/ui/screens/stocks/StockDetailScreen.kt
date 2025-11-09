package com.borsahalal.presentation.ui.screens.stocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.borsahalal.presentation.ui.components.EditStockDialog
import com.borsahalal.presentation.ui.components.HoldingsCard
import com.borsahalal.presentation.viewmodels.HoldingsViewModel
import com.borsahalal.presentation.viewmodels.StockViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailScreen(
    stockId: Long,
    viewModel: StockViewModel = koinViewModel(),
    holdingsViewModel: HoldingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val selectedStock by viewModel.selectedStock.collectAsState()
    val holdings by holdingsViewModel.holdings.collectAsState()
    val stockSummary by holdingsViewModel.stockSummary.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(stockId) {
        viewModel.selectStock(stockId)
        holdingsViewModel.loadStockHoldings(stockId)
    }

    Scaffold(
        topAppBar = {
            TopAppBar(
                title = { Text(selectedStock?.prefix ?: "Stock Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { padding ->
        selectedStock?.let { stock ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stock Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stock.prefix,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = stock.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Holdings Summary
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Holdings Summary",
                            style = MaterialTheme.typography.titleMedium
                        )

                        stockSummary?.let { summary ->
                            InfoRow("Total Shares", String.format("%.2f", summary.totalShares))
                            InfoRow("Average Price", String.format("%.2f", summary.averagePrice))
                            InfoRow("Current Value", String.format("%.2f", summary.currentValue))
                            InfoRow("Realized Profit", String.format("%.2f", summary.realizedProfit))
                        } ?: run {
                            InfoRow("Total Shares", "0.00")
                            InfoRow("Average Price", "0.00")
                            InfoRow("Current Value", "0.00")
                            InfoRow("Realized Profit", "0.00")
                        }
                    }
                }

                // FIFO Holdings Breakdown
                if (holdings.isNotEmpty()) {
                    HoldingsCard(
                        holdings = holdings,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Zakat Info
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Zakat Information",
                            style = MaterialTheme.typography.titleMedium
                        )

                        InfoRow("Zakat Percentage", "${stock.zakatPercentage}%")
                        stockSummary?.let { summary ->
                            val zakatDue = summary.currentValue * (stock.zakatPercentage / 100.0)
                            InfoRow("Estimated Zakat Due", String.format("%.2f", zakatDue))
                        } ?: run {
                            InfoRow("Estimated Zakat Due", "0.00")
                        }
                    }
                }

                // Notes
                if (!stock.notes.isNullOrBlank()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Notes",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stock.notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Edit Dialog
            if (showEditDialog) {
                EditStockDialog(
                    stock = stock,
                    onDismiss = { showEditDialog = false },
                    onConfirm = { name, zakatPercentage, notes ->
                        viewModel.updateStock(
                            stock.copy(
                                name = name,
                                zakatPercentage = zakatPercentage,
                                notes = notes
                            )
                        )
                        showEditDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
