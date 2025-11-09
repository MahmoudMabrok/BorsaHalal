package com.borsahalal.presentation.ui.screens.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.borsahalal.presentation.ui.components.DateRange
import com.borsahalal.presentation.ui.components.DateRangeFilter
import com.borsahalal.presentation.viewmodels.DashboardViewModel
import com.borsahalal.presentation.viewmodels.ProfileViewModel
import com.borsahalal.presentation.viewmodels.ReportsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReportsScreenWithExport(
    dashboardViewModel: DashboardViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel(),
    reportsViewModel: ReportsViewModel = koinViewModel()
) {
    val dashboardState by dashboardViewModel.state.collectAsState()
    val activeProfile by profileViewModel.activeProfile.collectAsState()
    val reportData by reportsViewModel.reportData.collectAsState()
    val selectedDateRange by reportsViewModel.selectedDateRange.collectAsState()

    var currentDateRange by remember { mutableStateOf<DateRange?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }

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
                    text = "Reports & Export",
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
                    Text(
                        text = "Portfolio Summary",
                        style = MaterialTheme.typography.titleMedium
                    )
                    QuickStat("Total Invested", String.format("%.2f %s",
                        dashboardState.totalInvested, activeProfile?.currency ?: "USD"))
                    QuickStat("Portfolio Value", String.format("%.2f %s",
                        dashboardState.totalPortfolioValue, activeProfile?.currency ?: "USD"))
                    QuickStat("Total Profit", String.format("%.2f %s",
                        dashboardState.totalProfit, activeProfile?.currency ?: "USD"))
                    QuickStat("Return", String.format("%.2f%%", dashboardState.returnPercentage))
                }
            }
        }

        // Date Range Filter
        item {
            DateRangeFilter(
                selectedRange = selectedDateRange,
                onRangeSelected = { option, range ->
                    reportsViewModel.updateDateRange(option)
                    currentDateRange = range
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Export Options
        item {
            Text(
                text = "Export to CSV",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExportCard(
                    title = "Portfolio Summary",
                    description = "Export complete portfolio metrics and statistics",
                    icon = Icons.Default.Assessment,
                    onClick = {
                        reportsViewModel.generatePortfolioSummaryReport()
                        showReportDialog = true
                    }
                )

                ExportCard(
                    title = "Stock Holdings",
                    description = "Export current stock holdings with average prices",
                    icon = Icons.Default.ShowChart,
                    onClick = {
                        reportsViewModel.generateStocksReport()
                        showReportDialog = true
                    }
                )

                ExportCard(
                    title = "Transaction History",
                    description = "Export all transactions (applies date filter)",
                    icon = Icons.Default.SwapHoriz,
                    onClick = {
                        reportsViewModel.generateTransactionReport(currentDateRange)
                        showReportDialog = true
                    }
                )
            }
        }
    }

    // Report Dialog
    if (showReportDialog && reportData.csvContent.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = {
                showReportDialog = false
                reportsViewModel.clearReport()
            },
            title = { Text("${reportData.reportType} - CSV Export") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Copy the CSV content below and save as .csv file:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextField(
                        value = reportData.csvContent,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .verticalScroll(rememberScrollState()),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showReportDialog = false
                    reportsViewModel.clearReport()
                }) {
                    Text("Close")
                }
            }
        )
    }

    // Loading Dialog
    if (reportData.isGenerating) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Generating Report...") },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                }
            },
            confirmButton = {}
        )
    }

    // Error Dialog
    reportData.error?.let { error ->
        AlertDialog(
            onDismissRequest = { reportsViewModel.clearReport() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { reportsViewModel.clearReport() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun QuickStat(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun ExportCard(
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
