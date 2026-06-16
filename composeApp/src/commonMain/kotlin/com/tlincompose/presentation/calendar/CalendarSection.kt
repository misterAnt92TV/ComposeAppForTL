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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tlincompose.core.*
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.accessibility.AccessibilitySettingsUiState
import com.tlincompose.presentation.catalog.ProjectIconAvatar
import com.tlincompose.presentation.components.StatusPill
import com.tlincompose.presentation.layout.AccessibilityLayoutSpec
import kotlinx.datetime.LocalDate

@Composable
internal fun CalendarSection(
    grid: List<MonthCellUiModel>,
    accessibilityState: AccessibilitySettingsUiState,
    layoutSpec: AccessibilityLayoutSpec,
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
            Text(
                text = strings.calendarTitle,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (layoutSpec.showSupportingCopy) {
                Text(
                    text = strings.calendarDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
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
@OptIn(ExperimentalLayoutApi::class)
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
    val borderColor = when {
        cell.isActivityDropTarget -> MaterialTheme.colorScheme.primary
        cell.isActivityDragSource -> MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
        cell.isRangeStart || cell.isRangeEnd -> MaterialTheme.colorScheme.tertiary
        cell.isToday -> MaterialTheme.colorScheme.secondary
        cell.holidayLabel != null -> MaterialTheme.colorScheme.primary.copy(alpha = 0.42f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
    }
    val backgroundColor = when {
        cell.isActivityDropTarget -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.88f)
        cell.isActivityDragSource -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
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
                    Text(
                        text = strings.compactHours(formatHours(cell.totalMinutes)),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
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
