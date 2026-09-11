package com.tlincompose.presentation.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.rotate
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tlincompose.core.BrandingLogoHeaderMaxHeightDp
import com.tlincompose.core.BrandingLogoHeaderMaxWidthDp
import com.tlincompose.core.appTagline
import com.tlincompose.core.brandingLogoContentDescription
import com.tlincompose.core.cancelRangeSelection
import com.tlincompose.core.closeSettingsLabel
import com.tlincompose.core.compactHours
import com.tlincompose.core.exportFormatsInfo
import com.tlincompose.core.exportSelectedRange
import com.tlincompose.core.exportVisibleMonth
import com.tlincompose.core.formatHours
import com.tlincompose.core.highContrastActive
import com.tlincompose.core.hideActivityCatalog
import com.tlincompose.core.monthCompletionComplete
import com.tlincompose.core.monthCompletionProgress
import com.tlincompose.core.monthCompletionRemaining
import com.tlincompose.core.monthHoursTitle
import com.tlincompose.core.openSettingsLabel
import com.tlincompose.core.percentageValue
import com.tlincompose.core.selectExportRange
import com.tlincompose.core.showActivityCatalog
import com.tlincompose.core.standardWorkday
import com.tlincompose.core.weekStartsMonday
import com.tlincompose.core.weekendNormallyFree
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
    monthSummary: MonthSummaryUiState,
    intervalMessage: String?,
    isSelectingRange: Boolean,
    isExportEnabled: Boolean,
    accessibilityState: AccessibilitySettingsUiState,
    brandingLogoBase64: String?,
    isSettingsVisible: Boolean,
    layoutSpec: AccessibilityLayoutSpec,
    onToggleRangeSelection: () -> Unit,
    onExport: () -> Unit,
    onToggleSettings: () -> Unit,
    isActivityCatalogVisible: Boolean,
    onToggleActivityCatalog: () -> Unit,
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
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val useCompactHeaderLayout = maxWidth < layoutSpec.headerCompactBreakpoint
                    if (useCompactHeaderLayout) {
                        CompactHeaderTopSection(
                            brandingLogoBase64 = brandingLogoBase64,
                            isSettingsVisible = isSettingsVisible,
                            layoutSpec = layoutSpec,
                            isActivityCatalogVisible = isActivityCatalogVisible,
                            onToggleSettings = onToggleSettings,
                            onToggleActivityCatalog = onToggleActivityCatalog,
                        )
                    } else {
                        WideHeaderTopSection(
                            brandingLogoBase64 = brandingLogoBase64,
                            isSettingsVisible = isSettingsVisible,
                            layoutSpec = layoutSpec,
                            isActivityCatalogVisible = isActivityCatalogVisible,
                            onToggleSettings = onToggleSettings,
                            onToggleActivityCatalog = onToggleActivityCatalog,
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
                MonthSummaryCard(
                    summary = monthSummary,
                    standardWorkdayMinutes = accessibilityState.standardWorkdayMinutes,
                    layoutSpec = layoutSpec,
                )
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val useStackedActions = maxWidth < layoutSpec.wrapActionsBreakpoint
                    if (useStackedActions) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            AppActionButton(
                                text = if (isSelectingRange) strings.cancelRangeSelection else strings.selectExportRange,
                                onClick = onToggleRangeSelection,
                                variant = AppButtonVariant.SECONDARY,
                                minHeight = layoutSpec.buttonMinHeight,
                                minWidth = layoutSpec.buttonPreferredWidth,
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = Icons.Filled.DateRange,
                                testTag = "range-selection-button",
                            )
                            AppActionButton(
                                text = if (isSelectingRange) strings.exportSelectedRange else strings.exportVisibleMonth,
                                onClick = onExport,
                                enabled = isExportEnabled,
                                minHeight = layoutSpec.buttonMinHeight,
                                minWidth = layoutSpec.buttonPreferredWidth,
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = Icons.AutoMirrored.Outlined.ExitToApp,
                                testTag = "export-button",
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            AppActionButton(
                                text = if (isSelectingRange) strings.cancelRangeSelection else strings.selectExportRange,
                                onClick = onToggleRangeSelection,
                                variant = AppButtonVariant.SECONDARY,
                                minHeight = layoutSpec.buttonMinHeight,
                                minWidth = layoutSpec.buttonPreferredWidth,
                                maxWidth = 220.dp,
                                modifier = Modifier.widthIn(
                                    min = layoutSpec.buttonPreferredWidth,
                                    max = 220.dp,
                                ),
                                leadingIcon = Icons.Filled.DateRange,
                                testTag = "range-selection-button",
                            )
                            AppActionButton(
                                text = if (isSelectingRange) strings.exportSelectedRange else strings.exportVisibleMonth,
                                onClick = onExport,
                                enabled = isExportEnabled,
                                minHeight = layoutSpec.buttonMinHeight,
                                minWidth = layoutSpec.buttonPreferredWidth,
                                maxWidth = 220.dp,
                                modifier = Modifier.widthIn(
                                    min = layoutSpec.buttonPreferredWidth,
                                    max = 220.dp,
                                ),
                                leadingIcon = Icons.AutoMirrored.Outlined.ExitToApp,
                                testTag = "export-button",
                            )
                        }
                    }
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
private fun WideHeaderTopSection(
    brandingLogoBase64: String?,
    isSettingsVisible: Boolean,
    layoutSpec: AccessibilityLayoutSpec,
    isActivityCatalogVisible: Boolean,
    onToggleSettings: () -> Unit,
    onToggleActivityCatalog: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        HeaderIdentityBlock(
            layoutSpec = layoutSpec,
            modifier = Modifier.weight(1f),
        )
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            WideHeaderControlsRow(
                brandingLogoBase64 = brandingLogoBase64,
                isSettingsVisible = isSettingsVisible,
                onToggleSettings = onToggleSettings,
            )
            ActivityCatalogToggleButton(
                isActivityCatalogVisible = isActivityCatalogVisible,
                minHeight = layoutSpec.buttonMinHeight,
                minWidth = 180.dp,
                maxWidth = 240.dp,
                onClick = onToggleActivityCatalog,
            )
        }
    }
}

@Composable
private fun CompactHeaderTopSection(
    brandingLogoBase64: String?,
    isSettingsVisible: Boolean,
    layoutSpec: AccessibilityLayoutSpec,
    isActivityCatalogVisible: Boolean,
    onToggleSettings: () -> Unit,
    onToggleActivityCatalog: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HeaderIdentityBlock(
            layoutSpec = layoutSpec,
            modifier = Modifier.fillMaxWidth(),
        )
        CompactHeaderControlsRow(
            brandingLogoBase64 = brandingLogoBase64,
            isSettingsVisible = isSettingsVisible,
            onToggleSettings = onToggleSettings,
        )
        ActivityCatalogToggleButton(
            isActivityCatalogVisible = isActivityCatalogVisible,
            onClick = onToggleActivityCatalog,
            minHeight = layoutSpec.buttonMinHeight,
            minWidth = layoutSpec.buttonPreferredWidth,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun HeaderIdentityBlock(
    layoutSpec: AccessibilityLayoutSpec,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current

    Column(
        modifier = modifier,
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
}

@Composable
private fun WideHeaderControlsRow(
    brandingLogoBase64: String?,
    isSettingsVisible: Boolean,
    onToggleSettings: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeaderBrandingLogo(
            brandingLogoBase64 = brandingLogoBase64,
        )
        SettingsToggleButton(
            isSettingsVisible = isSettingsVisible,
            onClick = onToggleSettings,
        )
    }
}

@Composable
private fun CompactHeaderControlsRow(
    brandingLogoBase64: String?,
    isSettingsVisible: Boolean,
    onToggleSettings: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        HeaderBrandingLogo(
            brandingLogoBase64 = brandingLogoBase64,
        )
        SettingsToggleButton(
            isSettingsVisible = isSettingsVisible,
            onClick = onToggleSettings,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

@Composable
private fun HeaderBrandingLogo(
    brandingLogoBase64: String?,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current

    BrandingLogoPreview(
        brandingLogoBase64 = brandingLogoBase64,
        contentDescription = strings.brandingLogoContentDescription(strings.appName),
        modifier = modifier.sizeIn(
            minWidth = BrandingLogoHeaderMaxWidthDp.dp,
            maxWidth = BrandingLogoHeaderMaxWidthDp.dp,
            minHeight = 42.dp,
            maxHeight = BrandingLogoHeaderMaxHeightDp.dp,
        ),
    )
}

@Composable
private fun ActivityCatalogToggleButton(
    isActivityCatalogVisible: Boolean,
    onClick: () -> Unit,
    minHeight: Dp,
    minWidth: Dp,
    modifier: Modifier = Modifier,
    maxWidth: Dp = Dp.Unspecified,
) {
    val strings = LocalAppStrings.current

    AppActionButton(
        text = if (isActivityCatalogVisible) {
            strings.hideActivityCatalog
        } else {
            strings.showActivityCatalog
        },
        onClick = onClick,
        modifier = modifier,
        variant = AppButtonVariant.SECONDARY,
        minHeight = minHeight,
        minWidth = minWidth,
        maxWidth = maxWidth,
        leadingIcon = Icons.AutoMirrored.Filled.MenuBook,
        testTag = "activity-catalog-toggle-button",
    )
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
    val animatedProgress by animateFloatAsState(
        targetValue = summary.completionFraction,
        animationSpec = tween(durationMillis = 220),
        label = "month-progress",
    )

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
                progress = { animatedProgress },
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
            modifier = Modifier
                .sizeIn(minWidth = 44.dp, minHeight = 44.dp)
                .semantics {
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
                modifier = Modifier.rotate(
                    animateFloatAsState(
                        targetValue = if (isSettingsVisible) 90f else 0f,
                        animationSpec = tween(180),
                        label = "settings-rotation",
                    ).value,
                ),
            )
        }
    }
}
