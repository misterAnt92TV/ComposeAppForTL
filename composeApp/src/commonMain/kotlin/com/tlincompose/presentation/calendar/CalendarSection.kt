package com.tlincompose.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Warning
import com.tlincompose.core.AppStrings
import com.tlincompose.core.activityCountPhrase
import com.tlincompose.core.compactHours
import com.tlincompose.core.currentDayPhrase
import com.tlincompose.core.dayCellDescription
import com.tlincompose.core.dailyHoursExceededLabel
import com.tlincompose.core.dailyHoursExceededMessage
import com.tlincompose.core.displayLabel
import com.tlincompose.core.formatHours
import com.tlincompose.core.insideSelectedRangePhrase
import com.tlincompose.core.moreActivities
import com.tlincompose.core.nextMonth
import com.tlincompose.core.openDayDetail
import com.tlincompose.core.outsideCurrentMonthPhrase
import com.tlincompose.core.previousMonth
import com.tlincompose.core.rangeEndPhrase
import com.tlincompose.core.rangeLabelBoth
import com.tlincompose.core.rangeLabelEnd
import com.tlincompose.core.rangeLabelStart
import com.tlincompose.core.rangeStartAndEndPhrase
import com.tlincompose.core.rangeStartPhrase
import com.tlincompose.core.todayLabel
import com.tlincompose.core.totalHoursPhrase
import com.tlincompose.core.weekdayLongLabelsMondayFirst
import com.tlincompose.core.weekdayShortLabelsMondayFirst
import com.tlincompose.presentation.header.MonthYearPickerDialog
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.accessibility.AccessibilitySettingsUiState
import com.tlincompose.presentation.catalog.ProjectIconAvatar
import com.tlincompose.presentation.components.StatusPill
import com.tlincompose.presentation.layout.AccessibilityLayoutSpec
import com.tlincompose.domain.model.CalendarMonth
import kotlinx.datetime.LocalDate

@Composable
internal fun CalendarSection(
    month: CalendarMonth,
    grid: List<MonthCellUiModel>,
    accessibilityState: AccessibilitySettingsUiState,
    layoutSpec: AccessibilityLayoutSpec,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthSelected: (CalendarMonth) -> Unit,
    onDaySelected: (LocalDate) -> Unit,
    onDayDragStarted: (LocalDate) -> Unit,
    onDayDragMoved: (LocalDate) -> Unit,
    onDayDragCompleted: () -> Unit,
    onDayDragCancelled: () -> Unit,
    onActivityDragStarted: (LocalDate, Int) -> Unit,
    onActivityDragMoved: (LocalDate) -> Unit,
    onActivityDragCompleted: () -> Unit,
    onActivityDragCancelled: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val cellBounds = remember { mutableStateMapOf<LocalDate, Rect>() }
    val showMonthDialog = remember { mutableStateOf(false) }
    val selectedMonth = remember(month) { mutableStateOf(month) }

    LaunchedEffect(grid) {
        val validDates = grid.map(MonthCellUiModel::date).toSet()
        cellBounds.keys
            .toList()
            .filterNot(validDates::contains)
            .forEach(cellBounds::remove)
    }

    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(layoutSpec.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(layoutSpec.contentPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            MonthSelectorBar(
                month = month,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onOpenPicker = { showMonthDialog.value = true },
            )
            WeekdayHeader()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calendar-grid"),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    grid.chunked(7).forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            week.forEach { cell ->
                                DayCell(
                                    cell = cell,
                                    accessibilityState = accessibilityState,
                                    layoutSpec = layoutSpec,
                                    onClick = { onDaySelected(cell.date) },
                                    onBoundsChanged = { bounds ->
                                        cellBounds[cell.date] = bounds
                                    },
                                    onDayDragStarted = onDayDragStarted,
                                    onDayDragMoved = onDayDragMoved,
                                    onDayDragCompleted = onDayDragCompleted,
                                    onDayDragCancelled = onDayDragCancelled,
                                    onActivityDragStarted = onActivityDragStarted,
                                    onActivityDragMoved = onActivityDragMoved,
                                    onActivityDragCompleted = onActivityDragCompleted,
                                    onActivityDragCancelled = onActivityDragCancelled,
                                    dateAtPosition = { positionInRoot ->
                                        findDateAtPosition(positionInRoot, cellBounds)
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }
            if (showMonthDialog.value) {
                MonthYearPickerDialog(
                    initialMonth = selectedMonth.value,
                    onDismissRequest = { showMonthDialog.value = false },
                    onMonthYearSelected = { newMonth ->
                        selectedMonth.value = newMonth
                        showMonthDialog.value = false
                        onMonthSelected(newMonth)
                    },
                )
            }
        }
    }
}

@Composable
private fun MonthSelectorBar(
    month: CalendarMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onOpenPicker: () -> Unit,
) {
    val strings = LocalAppStrings.current
    Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier
                    .sizeIn(minWidth = 32.dp, minHeight = 32.dp)
                    .testTag("previous-month-icon-button"),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = strings.previousMonth,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text = month.displayLabel(strings.language),
                modifier = Modifier
                    .clickable(onClick = onOpenPicker)
                    .padding(horizontal = 12.dp)
                    .weight(1f),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            IconButton(
                onClick = onNextMonth,
                modifier = Modifier
                    .sizeIn(minWidth = 32.dp, minHeight = 32.dp)
                    .testTag("next-month-icon-button"),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = strings.nextMonth,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun WeekdayHeader() {
    val strings = LocalAppStrings.current
    val labels = strings.weekdayShortLabelsMondayFirst.zip(strings.weekdayLongLabelsMondayFirst)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        labels.forEach { (shortLabel, fullLabel) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .semantics { contentDescription = fullLabel }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = shortLabel,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
private fun DayCell(
    cell: MonthCellUiModel,
    accessibilityState: AccessibilitySettingsUiState,
    layoutSpec: AccessibilityLayoutSpec,
    onClick: () -> Unit,
    onBoundsChanged: (Rect) -> Unit,
    onDayDragStarted: (LocalDate) -> Unit,
    onDayDragMoved: (LocalDate) -> Unit,
    onDayDragCompleted: () -> Unit,
    onDayDragCancelled: () -> Unit,
    onActivityDragStarted: (LocalDate, Int) -> Unit,
    onActivityDragMoved: (LocalDate) -> Unit,
    onActivityDragCompleted: () -> Unit,
    onActivityDragCancelled: () -> Unit,
    dateAtPosition: (Offset) -> LocalDate?,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val cellTopLeft = remember(cell.date) { mutableStateOf(Offset.Zero) }
    val totalHoursText = formatHours(cell.totalMinutes)
    val dailyLimitText = formatHours(cell.dailyLimitMinutes)
    val overLimitMessage = strings.dailyHoursExceededMessage(
        totalHours = totalHoursText,
        limitHours = dailyLimitText,
    )
    val borderColor = when {
        cell.isActivityDropTarget -> MaterialTheme.colorScheme.primary
        cell.isActivityDragSource -> MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
        cell.isOverDailyLimit -> MaterialTheme.colorScheme.error
        cell.isRangeStart || cell.isRangeEnd -> MaterialTheme.colorScheme.tertiary
        cell.isToday -> MaterialTheme.colorScheme.secondary
        cell.holidayLabel != null -> MaterialTheme.colorScheme.primary.copy(alpha = 0.42f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
    }
    val backgroundColor = when {
        cell.isActivityDropTarget -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.88f)
        cell.isActivityDragSource -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
        cell.isOverDailyLimit -> MaterialTheme.colorScheme.errorContainer.copy(
            alpha = if (cell.inCurrentMonth) 0.72f else 0.48f,
        )
        cell.isRangeStart || cell.isRangeEnd -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.82f)
        cell.isInSelectedRange -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
        !cell.inCurrentMonth -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.46f)
        cell.holidayLabel != null -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
        cell.isWeekend -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.42f)
        else -> MaterialTheme.colorScheme.surface
    }
    val rangeLabel = when {
        cell.isRangeStart && cell.isRangeEnd -> strings.rangeLabelBoth
        cell.isRangeStart -> strings.rangeLabelStart
        cell.isRangeEnd -> strings.rangeLabelEnd
        else -> null
    }
    val visibleActivities = cell.activityItems.take(layoutSpec.visibleSummaryCount)
    val extraActivities = cell.activityItems.size - visibleActivities.size

    Surface(
        modifier = modifier
            .aspectRatio(0.92f)
            .heightIn(min = layoutSpec.dayCellMinHeight)
            .border(
                width = if (accessibilityState.highContrast || cell.isToday || cell.isRangeStart || cell.isRangeEnd) {
                    2.dp
                } else if (cell.isOverDailyLimit) {
                    2.dp
                } else {
                    1.dp
                },
                color = borderColor,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
            )
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(22.dp))
            .pointerInput(cell.date) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        onDayDragStarted(cell.date)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        dateAtPosition(cellTopLeft.value + change.position)?.let(onDayDragMoved)
                    },
                    onDragEnd = onDayDragCompleted,
                    onDragCancel = onDayDragCancelled,
                )
            }
            .clickable(
                role = Role.Button,
                onClickLabel = strings.openDayDetail(cell.date),
                onClick = onClick,
            )
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInRoot()
                cellTopLeft.value = bounds.topLeft
                onBoundsChanged(bounds)
            }
            .semantics(mergeDescendants = true) {
                contentDescription = buildDayCellContentDescription(cell, strings)
            }
            .testTag("day-cell-${cell.date}"),
        color = backgroundColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(layoutSpec.dayCellPadding),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = cell.date.day.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (cell.inCurrentMonth) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = if (cell.isToday) FontWeight.Bold else FontWeight.Medium,
                )
                if (cell.totalMinutes > 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = strings.compactHours(totalHoursText),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (cell.isOverDailyLimit) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (cell.isOverDailyLimit) {
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                tooltip = {
                                    PlainTooltip {
                                        Text(
                                            text = overLimitMessage,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }
                                },
                                state = rememberTooltipState(),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = strings.dailyHoursExceededLabel,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .testTag("day-overlimit-alert-${cell.date}")
                                        .semantics {
                                            contentDescription = overLimitMessage
                                        },
                                )
                            }
                        }
                    }
                }
            }

            if (cell.isToday || rangeLabel != null || cell.activityCount > 0) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (cell.isToday) {
                        StatusPill(
                            text = strings.todayLabel,
                            background = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    rangeLabel?.let { label ->
                        StatusPill(
                            text = label,
                            background = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                    if (cell.activityCount > 0) {
                        StatusPill(
                            text = strings.activityCountPhrase(cell.activityCount),
                            background = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            cell.holidayLabel?.let { holiday ->
                Text(
                    text = holiday,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (visibleActivities.isEmpty()) {
                Spacer(modifier = Modifier.weight(1f, fill = true))
            } else {
                visibleActivities.forEach { activity ->
                    val activityTopLeft = remember(cell.date, activity.activityIndex) { mutableStateOf(Offset.Zero) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                activityTopLeft.value = coordinates.boundsInRoot().topLeft
                            }
                            .pointerInput(cell.date, activity.activityIndex) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        onActivityDragStarted(cell.date, activity.activityIndex)
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        dateAtPosition(activityTopLeft.value + change.position)?.let(onActivityDragMoved)
                                    },
                                    onDragEnd = onActivityDragCompleted,
                                    onDragCancel = onActivityDragCancelled,
                                )
                            }
                            .testTag("day-activity-${cell.date}-${activity.activityIndex}"),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ProjectIconAvatar(
                            entryType = activity.entryType,
                            projectIconPreset = null,
                            projectCustomIconBase64 = null,
                            title = activity.summary,
                            size = 20.dp,
                        )
                        Text(
                            text = activity.summary,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                if (extraActivities > 0) {
                    Text(
                        text = strings.moreActivities(extraActivities),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.weight(1f, fill = true))
            }
        }
    }
}

internal fun buildDayCellContentDescription(
    cell: MonthCellUiModel,
    strings: AppStrings,
): String {
    val parts = mutableListOf(strings.dayCellDescription(cell.date))

    if (cell.isToday) {
        parts += strings.currentDayPhrase
    }
    if (!cell.inCurrentMonth) {
        parts += strings.outsideCurrentMonthPhrase
    }
    if (cell.holidayLabel != null) {
        parts += cell.holidayLabel
    }
    if (cell.totalMinutes > 0) {
        parts += strings.totalHoursPhrase(formatHours(cell.totalMinutes))
    }
    if (cell.isOverDailyLimit) {
        parts += strings.dailyHoursExceededMessage(
            totalHours = formatHours(cell.totalMinutes),
            limitHours = formatHours(cell.dailyLimitMinutes),
        )
    }
    if (cell.activityCount > 0) {
        parts += strings.activityCountPhrase(cell.activityCount)
    }
    if (cell.isRangeStart && cell.isRangeEnd) {
        parts += strings.rangeStartAndEndPhrase
    } else if (cell.isRangeStart) {
        parts += strings.rangeStartPhrase
    } else if (cell.isRangeEnd) {
        parts += strings.rangeEndPhrase
    } else if (cell.isInSelectedRange) {
        parts += strings.insideSelectedRangePhrase
    }

    return parts.joinToString(separator = ", ")
}

private fun findDateAtPosition(
    positionInRoot: Offset,
    cellBounds: Map<LocalDate, Rect>,
): LocalDate? = cellBounds.entries.firstOrNull { (_, bounds) ->
    bounds.contains(positionInRoot)
}?.key
