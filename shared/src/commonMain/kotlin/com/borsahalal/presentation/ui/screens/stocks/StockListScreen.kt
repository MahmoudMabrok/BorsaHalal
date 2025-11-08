package com.borsahalal.presentation.ui.screens.stocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.borsahalal.presentation.viewmodels.StockViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StockListScreen(
    viewModel: StockViewModel = koinViewModel(),
    onStockClick: (Long) -> Unit = {}
) {
    val stocks by viewModel.stocks.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Stocks",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(stocks) { stock ->
                Card(
                    modifier = Modifier.fillMaxSize(),
                    onClick = { onStockClick(stock.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${stock.prefix} - ${stock.name}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Zakat: ${stock.zakatPercentage}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
