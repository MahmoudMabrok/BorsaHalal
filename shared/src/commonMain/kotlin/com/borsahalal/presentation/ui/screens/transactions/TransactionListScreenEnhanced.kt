package com.borsahalal.presentation.ui.screens.transactions

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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.borsahalal.data.database.entities.TransactionType
import com.borsahalal.presentation.ui.components.TransactionCard
import com.borsahalal.presentation.viewmodels.TransactionViewModel
import org.koin.compose.viewmodel.koinViewModel

enum class TransactionSortOption {
    DATE_DESC, DATE_ASC, AMOUNT_DESC, AMOUNT_ASC
}

@Composable
fun TransactionListScreenEnhanced(
    viewModel: TransactionViewModel = koinViewModel(),
    onAddTransaction: () -> Unit = {},
    onTransactionClick: (Long) -> Unit = {}
) {
    val transactions by viewModel.transactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var filterType by remember { mutableStateOf<TransactionType?>(null) }
    var sortOption by remember { mutableStateOf(TransactionSortOption.DATE_DESC) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Apply filters and sorting
    val filteredAndSortedTransactions = transactions
        .let { list ->
            when (filterType) {
                null -> list
                else -> list.filter { it.type == filterType }
            }
        }
        .let { list ->
            when (sortOption) {
                TransactionSortOption.DATE_DESC -> list.sortedByDescending { it.date }
                TransactionSortOption.DATE_ASC -> list.sortedBy { it.date }
                TransactionSortOption.AMOUNT_DESC -> list.sortedByDescending { it.quantity * it.pricePerUnit }
                TransactionSortOption.AMOUNT_ASC -> list.sortedBy { it.quantity * it.pricePerUnit }
            }
        }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header with sort button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.headlineLarge
                )

                IconButton(onClick = { showSortMenu = true }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Sort & Filter")
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    Text(
                        "Sort by",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    DropdownMenuItem(
                        text = { Text("Date (Newest)") },
                        onClick = {
                            sortOption = TransactionSortOption.DATE_DESC
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Date (Oldest)") },
                        onClick = {
                            sortOption = TransactionSortOption.DATE_ASC
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Amount (Highest)") },
                        onClick = {
                            sortOption = TransactionSortOption.AMOUNT_DESC
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Amount (Lowest)") },
                        onClick = {
                            sortOption = TransactionSortOption.AMOUNT_ASC
                            showSortMenu = false
                        }
                    )
                }
            }

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterType == null,
                    onClick = { filterType = null },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = filterType == TransactionType.BUY,
                    onClick = { filterType = TransactionType.BUY },
                    label = { Text("Buy") }
                )
                FilterChip(
                    selected = filterType == TransactionType.SELL,
                    onClick = { filterType = TransactionType.SELL },
                    label = { Text("Sell") }
                )
            }

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                filteredAndSortedTransactions.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (transactions.isEmpty()) "No transactions yet" else "No matching transactions",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (transactions.isEmpty()) "Tap + to add your first transaction" else "Try adjusting your filters",
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
                        items(filteredAndSortedTransactions) { transaction ->
                            TransactionCard(
                                transaction = transaction,
                                onClick = { onTransactionClick(transaction.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
