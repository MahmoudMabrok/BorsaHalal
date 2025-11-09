package com.borsahalal.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun QuickActionsCard(
    onAddStock: () -> Unit = {},
    onAddTransaction: () -> Unit = {},
    onViewReports: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionItem(
                    icon = Icons.Default.Business,
                    label = "Add Stock",
                    onClick = onAddStock,
                    modifier = Modifier.weight(1f)
                )

                QuickActionItem(
                    icon = Icons.Default.SwapHoriz,
                    label = "Add Transaction",
                    onClick = onAddTransaction,
                    modifier = Modifier.weight(1f)
                )

                QuickActionItem(
                    icon = Icons.Default.Assessment,
                    label = "View Reports",
                    onClick = onViewReports,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier
                    .padding(12.dp)
                    .size(24.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
