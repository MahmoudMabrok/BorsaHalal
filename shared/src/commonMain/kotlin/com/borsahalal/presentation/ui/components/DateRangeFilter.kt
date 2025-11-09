package com.borsahalal.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*

enum class DateRangeOption {
    ALL_TIME, LAST_7_DAYS, LAST_30_DAYS, LAST_3_MONTHS, LAST_6_MONTHS, LAST_YEAR, CUSTOM
}

data class DateRange(
    val startDate: Long,
    val endDate: Long
)

@Composable
fun DateRangeFilter(
    selectedRange: DateRangeOption,
    onRangeSelected: (DateRangeOption, DateRange?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomDialog by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Date Range",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedRange == DateRangeOption.ALL_TIME,
                    onClick = { onRangeSelected(DateRangeOption.ALL_TIME, null) },
                    label = { Text("All Time") }
                )

                FilterChip(
                    selected = selectedRange == DateRangeOption.LAST_30_DAYS,
                    onClick = {
                        val endDate = Clock.System.now().toEpochMilliseconds()
                        val startDate = Clock.System.now()
                            .minus(30, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                            .toEpochMilliseconds()
                        onRangeSelected(DateRangeOption.LAST_30_DAYS, DateRange(startDate, endDate))
                    },
                    label = { Text("30 Days") }
                )

                FilterChip(
                    selected = selectedRange == DateRangeOption.LAST_YEAR,
                    onClick = {
                        val endDate = Clock.System.now().toEpochMilliseconds()
                        val startDate = Clock.System.now()
                            .minus(365, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                            .toEpochMilliseconds()
                        onRangeSelected(DateRangeOption.LAST_YEAR, DateRange(startDate, endDate))
                    },
                    label = { Text("1 Year") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedRange == DateRangeOption.LAST_3_MONTHS,
                    onClick = {
                        val endDate = Clock.System.now().toEpochMilliseconds()
                        val startDate = Clock.System.now()
                            .minus(90, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                            .toEpochMilliseconds()
                        onRangeSelected(DateRangeOption.LAST_3_MONTHS, DateRange(startDate, endDate))
                    },
                    label = { Text("3 Months") }
                )

                FilterChip(
                    selected = selectedRange == DateRangeOption.LAST_6_MONTHS,
                    onClick = {
                        val endDate = Clock.System.now().toEpochMilliseconds()
                        val startDate = Clock.System.now()
                            .minus(180, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                            .toEpochMilliseconds()
                        onRangeSelected(DateRangeOption.LAST_6_MONTHS, DateRange(startDate, endDate))
                    },
                    label = { Text("6 Months") }
                )
            }
        }
    }
}
