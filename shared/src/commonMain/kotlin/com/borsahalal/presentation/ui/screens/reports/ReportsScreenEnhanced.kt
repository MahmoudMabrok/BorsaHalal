package com.borsahalal.presentation.ui.screens.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.borsahalal.presentation.viewmodels.DashboardViewModel
import com.borsahalal.presentation.viewmodels.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReportsScreenEnhanced(
    dashboardViewModel: DashboardViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val dashboardState by dashboardViewModel.state.collectAsState()
    val activeProfile by profileViewModel.activeProfile.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Reports & Analytics",
                    style = MaterialTheme.typography.headlineLarge
                )
                activeProfile?.let { profile ->
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Summary Section
        item {
            Text(
                text = "Portfolio Summary",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryRow(
                        label = "Total Portfolio Value",
                        value = String.format("%.2f %s",
                            dashboardState.totalPortfolioValue,
                            activeProfile?.currency ?: "USD"
                        )
                    )
                    SummaryRow(
                        label = "Realized Profit/Loss",
                        value = String.format("%.2f %s",
                            dashboardState.totalRealizedProfit,
                            activeProfile?.currency ?: "USD"
                        ),
                        valueColor = if (dashboardState.totalRealizedProfit >= 0)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                    SummaryRow(
                        label = "Unrealized Profit/Loss",
                        value = String.format("%.2f %s",
                            dashboardState.totalUnrealizedProfit,
                            activeProfile?.currency ?: "USD"
                        ),
                        valueColor = if (dashboardState.totalUnrealizedProfit >= 0)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                    SummaryRow(
                        label = "Active Stocks",
                        value = dashboardState.stockCount.toString()
                    )
                    SummaryRow(
                        label = "Annual Zakat Due",
                        value = String.format("%.2f %s",
                            dashboardState.totalZakatDue,
                            activeProfile?.currency ?: "USD"
                        )
                    )
                }
            }
        }

        // Available Reports
        item {
            Text(
                text = "Available Reports",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReportCard(
                    title = "Profit & Loss Statement",
                    description = "Detailed breakdown of realized profits and losses",
                    icon = Icons.Default.Assessment,
                    onClick = { /* TODO: Generate P&L Report */ }
                )

                ReportCard(
                    title = "Portfolio Performance",
                    description = "Overall portfolio performance and returns",
                    icon = Icons.Default.ShowChart,
                    onClick = { /* TODO: Generate Performance Report */ }
                )

                ReportCard(
                    title = "Transaction History",
                    description = "Complete history of all transactions",
                    icon = Icons.Default.SwapHoriz,
                    onClick = { /* TODO: Generate Transaction Report */ }
                )

                ReportCard(
                    title = "Zakat Summary",
                    description = "Annual Zakat calculations and due amounts",
                    icon = Icons.Default.Description,
                    onClick = { /* TODO: Generate Zakat Report */ }
                )

                ReportCard(
                    title = "Export Portfolio",
                    description = "Export all data to CSV or Excel",
                    icon = Icons.Default.Download,
                    onClick = { /* TODO: Export Portfolio */ }
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor
        )
    }
}

@Composable
private fun ReportCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = { Text(title) },
            supportingContent = { Text(description) },
            leadingContent = {
                Icon(icon, contentDescription = title)
            }
        )
    }
}
