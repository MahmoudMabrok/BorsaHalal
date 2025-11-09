package com.borsahalal.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.borsahalal.presentation.ui.theme.Loss
import com.borsahalal.presentation.ui.theme.Profit

@Composable
fun SummaryCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    trendValue: Double? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
            // Title row with optional icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Value
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Subtitle and trend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                trendValue?.let { trend ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (trend >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = if (trend >= 0) "Profit" else "Loss",
                            tint = if (trend >= 0) Profit else Loss
                        )
                        Text(
                            text = String.format("%.2f%%", kotlin.math.abs(trend)),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (trend >= 0) Profit else Loss
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfitLossCard(
    title: String,
    value: Double,
    currency: String = "USD",
    modifier: Modifier = Modifier
) {
    val isProfit = value >= 0
    val color = if (isProfit) Profit else Loss

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isProfit) {
                Profit.copy(alpha = 0.1f)
            } else {
                Loss.copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isProfit) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = if (isProfit) "Profit" else "Loss",
                    tint = color
                )

                Text(
                    text = "${if (isProfit) "+" else ""}${String.format("%.2f", value)} $currency",
                    style = MaterialTheme.typography.headlineMedium,
                    color = color
                )
            }
        }
    }
}
