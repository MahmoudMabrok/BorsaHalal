package com.borsahalal.presentation.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.borsahalal.presentation.ui.components.ProfitLossCard
import com.borsahalal.presentation.ui.components.SummaryCard
import com.borsahalal.presentation.viewmodels.DashboardViewModel
import com.borsahalal.presentation.viewmodels.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreenEnhanced(
    dashboardViewModel: DashboardViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val state by dashboardViewModel.state.collectAsState()
    val activeProfile by profileViewModel.activeProfile.collectAsState()

    LaunchedEffect(Unit) {
        dashboardViewModel.loadDashboardData()
    }

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
                    text = "Dashboard",
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

        if (state.isLoading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            // Summary Cards Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    item {
                        SummaryCard(
                            title = "Portfolio Value",
                            value = String.format("%.2f", state.totalPortfolioValue),
                            subtitle = activeProfile?.currency ?: "USD",
                            icon = Icons.Default.AccountBalance,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    }

                    item {
                        SummaryCard(
                            title = "Total Stocks",
                            value = state.stockCount.toString(),
                            subtitle = "Active holdings",
                            icon = Icons.Default.ShowChart,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    }

                    item {
                        SummaryCard(
                            title = "Zakat Due",
                            value = String.format("%.2f", state.totalZakatDue),
                            subtitle = activeProfile?.currency ?: "USD",
                            icon = Icons.Default.Assessment,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    }
                }
            }

            // Profit/Loss Cards
            item {
                Text(
                    text = "Performance",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfitLossCard(
                        title = "Realized P/L",
                        value = state.totalRealizedProfit,
                        currency = activeProfile?.currency ?: "USD",
                        modifier = Modifier.weight(1f)
                    )

                    ProfitLossCard(
                        title = "Unrealized P/L",
                        value = state.totalUnrealizedProfit,
                        currency = activeProfile?.currency ?: "USD",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Stats
            item {
                Text(
                    text = "Quick Stats",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickStatRow(
                            label = "Total Portfolio Value",
                            value = String.format("%.2f %s",
                                state.totalPortfolioValue,
                                activeProfile?.currency ?: "USD"
                            )
                        )
                        QuickStatRow(
                            label = "Total Realized Profit",
                            value = String.format("%.2f %s",
                                state.totalRealizedProfit,
                                activeProfile?.currency ?: "USD"
                            )
                        )
                        QuickStatRow(
                            label = "Total Unrealized Profit",
                            value = String.format("%.2f %s",
                                state.totalUnrealizedProfit,
                                activeProfile?.currency ?: "USD"
                            )
                        )
                        QuickStatRow(
                            label = "Active Stocks",
                            value = state.stockCount.toString()
                        )
                        QuickStatRow(
                            label = "Annual Zakat Due",
                            value = String.format("%.2f %s",
                                state.totalZakatDue,
                                activeProfile?.currency ?: "USD"
                            )
                        )
                    }
                }
            }

            // Error state
            state.error?.let { error ->
                item {
                    Text(
                        text = "Error: $error",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
