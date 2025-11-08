package com.borsahalal.presentation.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.borsahalal.presentation.viewmodels.DashboardViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "BorsaHalal Dashboard",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Portfolio Value: ${state.totalPortfolioValue}",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Realized Profit: ${state.totalRealizedProfit}",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Unrealized Profit: ${state.totalUnrealizedProfit}",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Stocks: ${state.stockCount}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
