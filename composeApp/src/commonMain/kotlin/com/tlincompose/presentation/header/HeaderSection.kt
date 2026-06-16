package com.tlincompose.presentation.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tlincompose.core.*
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.accessibility.AccessibilitySettingsUiState
import com.tlincompose.presentation.calendar.MonthSummaryUiState
import com.tlincompose.presentation.components.AppActionButton
import com.tlincompose.presentation.components.AppButtonVariant
import com.tlincompose.presentation.components.BrandingLogoPreview
import com.tlincompose.presentation.components.HeaderInfoPill
import com.tlincompose.presentation.layout.AccessibilityLayoutSpec

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun HeaderSection(
    month: CalendarMonth,
    monthSummary: MonthSummaryUiState,
    intervalMessage: String?,
    isSelectingRange: Boolean,
    isExportEnabled: Boolean,
    accessibilityState: AccessibilitySettingsUiState,
    brandingLogoBase64: String?,
    isSettingsVisible: Boolean,
    layoutSpec: AccessibilityLayoutSpec,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToggleRangeSelection: () -> Unit,
    onExport: () -> Unit,
    onToggleSettings: () -> Unit,
) {
    val strings = LocalAppStrings.current
    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(layoutSpec.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.88f),
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f),
                        ),
                    ),
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(layoutSpec.headerPadding),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = strings.appName,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                        )
                        if (layoutSpec.showExtendedHeaderCopy) {
                            Text(
                                text = strings.appTagline,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.86f),
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BrandingLogoPreview(
                            brandingLogoBase64 = brandingLogoBase64,
                            contentDescription = strings.brandingLogoContentDescription(strings.appName),
                            modifier = Modifier.sizeIn(
                                minWidth = BrandingLogoHeaderMaxWidthDp.dp,
                                maxWidth = BrandingLogoHeaderMaxWidthDp.dp,
                                minHeight = 42.dp,
                                maxHeight = BrandingLogoHeaderMaxHeightDp.dp,
                            ),
                        )
                        SettingsToggleButton(
                            isSettingsVisible = isSettingsVisible,
                            onClick = onToggleSettings,
                        )
                    }
                }
                if (layoutSpec.showSupportingCopy) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        HeaderInfoPill(text = strings.weekStartsMonday)
                        HeaderInfoPill(text = strings.standardWorkday(formatHours(accessibilityState.standardWorkdayMinutes)))
                        HeaderInfoPill(text = strings.weekendNormallyFree)
                        HeaderInfoPill(text = strings.exportFormatsInfo)
                        if (accessibilityState.highContrast) {
                            HeaderInfoPill(text = strings.highContrastActive)
                        }
                    }
                }
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                ) {
                    Text(
                        text = month.displayLabel(strings.language),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                }
                MonthSummaryCard(
                    summary = monthSummary,
                    standardWorkdayMinutes = accessibilityState.standardWorkdayMinutes,
                    layoutSpec = layoutSpec,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AppActionButton(
                        text = strings.previousMonth,
                        onClick = onPreviousMonth,
                        variant = AppButtonVariant.SECONDARY,
                        minHeight = layoutSpec.buttonMinHeight,
                        modifier = Modifier.weight(1f),
                        testTag = "previous-month-button",
                    )
                    AppActionButton(
                        text = strings.nextMonth,
                        onClick = onNextMonth,
                        variant = AppButtonVariant.SECONDARY,
                        minHeight = layoutSpec.buttonMinHeight,
                        modifier = Modifier.weight(1f),
                        testTag = "next-month-button",
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AppActionButton(
                        text = if (isSelectingRange) {
                            strings.cancelRangeSelection
                        } else {
                            strings.selectExportRange
                        },
                        onClick = onToggleRangeSelection,
                        variant = AppButtonVariant.SECONDARY,
                        minHeight = layoutSpec.buttonMinHeight,
                        modifier = Modifier.weight(1f),
                        testTag = "range-selection-button",
                    )
                    AppActionButton(
                        text = if (isSelectingRange) {
                            strings.exportSelectedRange
                        } else {
                            strings.exportVisibleMonth
                        },
                        onClick = onExport,
                        enabled = isExportEnabled,
                        minHeight = layoutSpec.buttonMinHeight,
                        modifier = Modifier.weight(1f),
                        testTag = "export-button",
                    )
                }
                intervalMessage?.let { message ->
                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.08f),
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthSummaryCard(
    summary: MonthSummaryUiState,
    standardWorkdayMinutes: Int,
    layoutSpec: AccessibilityLayoutSpec,
) {
    val strings = LocalAppStrings.current
    val totalHoursText = formatHours(summary.totalLoggedMinutes)
    val completedHoursText = formatHours(summary.completedCompletionMinutes)
    val targetHoursText = formatHours(summary.targetCompletionMinutes)
    val remainingHoursText = formatHours(summary.remainingCompletionMinutes)
    val standardHoursText = formatHours(standardWorkdayMinutes)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("month-summary-card"),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(layoutSpec.cardCornerRadius),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = strings.monthHoursTitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = strings.compactHours(totalHoursText),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = strings.percentageValue(summary.completionPercentage),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Text(
                text = strings.monthCompletionProgress(
                    completedHours = completedHoursText,
                    targetHours = targetHoursText,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            LinearProgressIndicator(
                progress = { summary.completionFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(999.dp))
                    .testTag("month-completion-progress"),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
            )
            Text(
                text = if (summary.remainingCompletionMinutes > 0) {
                    strings.monthCompletionRemaining(remainingHoursText)
                } else {
                    strings.monthCompletionComplete(standardHoursText)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SettingsToggleButton(
    isSettingsVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val containerColor = if (isSettingsVisible) {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.92f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.78f)
    }
    val contentColor = if (isSettingsVisible) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = modifier.testTag("settings-toggle-button"),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = containerColor,
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.semantics {
                contentDescription = if (isSettingsVisible) {
                    strings.closeSettingsLabel
                } else {
                    strings.openSettingsLabel
                }
            },
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = null,
                tint = contentColor,
            )
        }
    }
}
