package com.borsahalal.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.borsahalal.data.database.entities.Transaction
import com.borsahalal.data.database.entities.TransactionType
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TransactionCard(
    transaction: Transaction,
    stockPrefix: String = "",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val containerColor = when (transaction.type) {
        TransactionType.BUY -> MaterialTheme.colorScheme.primaryContainer
        TransactionType.SELL -> MaterialTheme.colorScheme.tertiaryContainer
    }

    val contentColor = when (transaction.type) {
        TransactionType.BUY -> MaterialTheme.colorScheme.onPrimaryContainer
        TransactionType.SELL -> MaterialTheme.colorScheme.onTertiaryContainer
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = transaction.type.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor
                    )
                    if (stockPrefix.isNotEmpty()) {
                        Text(
                            text = stockPrefix,
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor.copy(alpha = 0.7f)
                        )
                    }
                }

                val date = Instant.fromEpochMilliseconds(transaction.date)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                Text(
                    text = "${date.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }

            // Transaction Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                    Text(
                        text = String.format("%.2f", transaction.quantity),
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Price/Unit",
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                    Text(
                        text = String.format("%.2f", transaction.pricePerUnit),
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor
                    )
                }
            }

            // Total and Commission
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                    Text(
                        text = String.format("%.2f", transaction.quantity * transaction.pricePerUnit),
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor
                    )
                }

                if (transaction.commission > 0) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Commission",
                            style = MaterialTheme.typography.labelSmall,
                            color = contentColor.copy(alpha = 0.7f)
                        )
                        Text(
                            text = String.format("%.2f", transaction.commission),
                            style = MaterialTheme.typography.bodyMedium,
                            color = contentColor
                        )
                    }
                }
            }

            // Notes if available
            transaction.notes?.let { notes ->
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f),
                    maxLines = 2
                )
            }
        }
    }
}
