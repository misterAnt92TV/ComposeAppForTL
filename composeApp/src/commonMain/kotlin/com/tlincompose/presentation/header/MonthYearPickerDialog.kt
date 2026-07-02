package com.tlincompose.presentation.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tlincompose.core.cancelLabel
import com.tlincompose.core.monthFieldLabel
import com.tlincompose.core.monthNames
import com.tlincompose.core.monthYearPickerTitle
import com.tlincompose.core.saveLabel
import com.tlincompose.core.yearFieldLabel
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.presentation.LocalAppStrings

@Composable
internal fun MonthYearPickerDialog(
    initialMonth: CalendarMonth,
    onDismissRequest: () -> Unit,
    onMonthYearSelected: (CalendarMonth) -> Unit,
) {
    val strings = LocalAppStrings.current
    var selectedMonthNumber by remember(initialMonth) { mutableIntStateOf(initialMonth.monthNumber) }
    var selectedYear by remember(initialMonth) { mutableIntStateOf(initialMonth.year) }
    var isMonthMenuExpanded by remember { mutableStateOf(false) }
    var isYearMenuExpanded by remember { mutableStateOf(false) }
    val years = remember(initialMonth.year) { (initialMonth.year - 10..initialMonth.year + 10).toList() }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = strings.monthYearPickerTitle,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = strings.monthFieldLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { isMonthMenuExpanded = true }) {
                        Text(strings.monthNames[selectedMonthNumber - 1])
                    }
                    DropdownMenu(
                        expanded = isMonthMenuExpanded,
                        onDismissRequest = { isMonthMenuExpanded = false },
                    ) {
                        strings.monthNames.forEachIndexed { index, monthName ->
                            DropdownMenuItem(
                                text = { Text(monthName) },
                                onClick = {
                                    selectedMonthNumber = index + 1
                                    isMonthMenuExpanded = false
                                },
                            )
                        }
                    }
                }
                Text(
                    text = strings.yearFieldLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { isYearMenuExpanded = true }) {
                        Text(selectedYear.toString())
                    }
                    DropdownMenu(
                        expanded = isYearMenuExpanded,
                        onDismissRequest = { isYearMenuExpanded = false },
                    ) {
                        years.forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year.toString()) },
                                onClick = {
                                    selectedYear = year
                                    isYearMenuExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onMonthYearSelected(CalendarMonth(selectedYear, selectedMonthNumber))
                },
            ) {
                Text(strings.saveLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(strings.cancelLabel)
            }
        },
    )
}
