package com.tlincompose.presentation.accessibility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tlincompose.core.AppStrings
import com.tlincompose.core.brandingDescription
import com.tlincompose.core.brandingLogoContentDescription
import com.tlincompose.core.brandingTitle
import com.tlincompose.core.closeLabel
import com.tlincompose.core.comfortableLayoutDescription
import com.tlincompose.core.comfortableLayoutTitle
import com.tlincompose.core.decreaseWorkdayButtonLabel
import com.tlincompose.core.decreaseWorkdayHours
import com.tlincompose.core.focusModeDescription
import com.tlincompose.core.focusModeTitle
import com.tlincompose.core.formatHours
import com.tlincompose.core.highContrastDescription
import com.tlincompose.core.highContrastTitle
import com.tlincompose.core.increaseWorkdayButtonLabel
import com.tlincompose.core.increaseWorkdayHours
import com.tlincompose.core.languageDescription
import com.tlincompose.core.languageLabel
import com.tlincompose.core.languageTitle
import com.tlincompose.core.pdfExportStyleDescription
import com.tlincompose.core.pdfExportStyleLabel
import com.tlincompose.core.pdfExportStyleOptionDescription
import com.tlincompose.core.pdfExportStyleTitle
import com.tlincompose.core.privacyBody
import com.tlincompose.core.privacyDescription
import com.tlincompose.core.privacyTitle
import com.tlincompose.core.removeBrandingLogoLabel
import com.tlincompose.core.replaceBrandingLogoLabel
import com.tlincompose.core.settingsDescription
import com.tlincompose.core.settingsTitle
import com.tlincompose.core.standardWorkday
import com.tlincompose.core.textSizeTitle
import com.tlincompose.core.themeTitle
import com.tlincompose.core.uploadBrandingLogoLabel
import com.tlincompose.core.workdayHoursDescription
import com.tlincompose.core.workdayHoursTitle
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.PdfExportStyle
import com.tlincompose.presentation.BrandLogoPickerLauncher
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.components.AppActionButton
import com.tlincompose.presentation.components.AppButtonVariant
import com.tlincompose.presentation.components.BrandingLogoPreview
import com.tlincompose.presentation.layout.AccessibilityLayoutSpec

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun AccessibilitySettingsSection(
    state: AccessibilitySettingsUiState,
    layoutSpec: AccessibilityLayoutSpec,
    brandLogoPickerLauncher: BrandLogoPickerLauncher,
    onThemeModeChanged: (ThemeModeUiState) -> Unit,
    onTextScaleChanged: (AccessibilityTextScaleUiState) -> Unit,
    onHighContrastChanged: (Boolean) -> Unit,
    onComfortableSpacingChanged: (Boolean) -> Unit,
    onFocusModeChanged: (Boolean) -> Unit,
    onLanguageChanged: (AppLanguage) -> Unit,
    onStandardWorkdayChanged: (Int) -> Unit,
    onPdfExportStyleChanged: (PdfExportStyleUiState) -> Unit,
    onPickBrandingLogo: (ByteArray) -> Unit,
    onClearBrandingLogo: () -> Unit,
    onClose: () -> Unit,
) {
    val strings = LocalAppStrings.current
    Card(
        modifier = Modifier.testTag("accessibility-settings-section"),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(layoutSpec.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(layoutSpec.contentPadding)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = strings.settingsTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = strings.settingsDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                AppActionButton(
                    text = strings.closeLabel,
                    onClick = onClose,
                    variant = AppButtonVariant.TERTIARY,
                    minHeight = layoutSpec.buttonMinHeight,
                    modifier = Modifier.testTag("settings-close-button"),
                )
            }
            Text(
                text = strings.themeTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            FlowRow(
                modifier = Modifier.testTag("theme-mode-group"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ThemeModeUiState.entries.forEach { option ->
                    FilterChip(
                        selected = state.themeMode == option,
                        onClick = { onThemeModeChanged(option) },
                        label = {
                            Text(
                                text = option.label(strings),
                                maxLines = 2,
                            )
                        },
                        modifier = Modifier.sizeIn(minHeight = layoutSpec.buttonMinHeight - 4.dp),
                    )
                }
            }
            Text(
                text = state.themeMode.description(strings),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                text = strings.textSizeTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 12.dp),
            )
            FlowRow(
                modifier = Modifier.testTag("accessibility-text-scale-group"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AccessibilityTextScaleUiState.entries.forEach { option ->
                    FilterChip(
                        selected = state.textScale == option,
                        onClick = { onTextScaleChanged(option) },
                        label = {
                            Text(
                                text = option.label(strings),
                                maxLines = 2,
                            )
                        },
                        modifier = Modifier.sizeIn(minHeight = layoutSpec.buttonMinHeight - 4.dp),
                    )
                }
            }
            Text(
                text = state.textScale.description(strings),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = strings.workdayHoursTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = strings.workdayHoursDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AppActionButton(
                            text = strings.decreaseWorkdayButtonLabel,
                            onClick = {
                                onStandardWorkdayChanged((state.standardWorkdayMinutes - 30).coerceAtLeast(60))
                            },
                            variant = AppButtonVariant.SECONDARY,
                            minHeight = layoutSpec.buttonMinHeight,
                            modifier = Modifier.semantics {
                                contentDescription = strings.decreaseWorkdayHours
                            },
                        )
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                        ) {
                            Text(
                                text = strings.standardWorkday(formatHours(state.standardWorkdayMinutes)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        AppActionButton(
                            text = strings.increaseWorkdayButtonLabel,
                            onClick = {
                                onStandardWorkdayChanged((state.standardWorkdayMinutes + 30).coerceAtMost(16 * 60))
                            },
                            variant = AppButtonVariant.SECONDARY,
                            minHeight = layoutSpec.buttonMinHeight,
                            modifier = Modifier.semantics {
                                contentDescription = strings.increaseWorkdayHours
                            },
                        )
                    }
                }
            }
            AccessibilityOptionRow(
                title = strings.highContrastTitle,
                description = strings.highContrastDescription,
                checked = state.highContrast,
                layoutSpec = layoutSpec,
                onCheckedChange = onHighContrastChanged,
                modifier = Modifier.testTag("high-contrast-toggle"),
            )
            AccessibilityOptionRow(
                title = strings.comfortableLayoutTitle,
                description = strings.comfortableLayoutDescription,
                checked = state.comfortableSpacing,
                layoutSpec = layoutSpec,
                onCheckedChange = onComfortableSpacingChanged,
                modifier = Modifier.testTag("comfortable-spacing-toggle"),
            )
            AccessibilityOptionRow(
                title = strings.focusModeTitle,
                description = strings.focusModeDescription,
                checked = state.focusMode,
                layoutSpec = layoutSpec,
                onCheckedChange = onFocusModeChanged,
                modifier = Modifier.testTag("focus-mode-toggle"),
            )
            Text(
                text = strings.languageTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = strings.languageDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AppLanguage.entries.forEach { option ->
                    FilterChip(
                        selected = state.language == option,
                        onClick = { onLanguageChanged(option) },
                        label = {
                            Text(
                                text = strings.languageLabel(option),
                                maxLines = 2,
                            )
                        },
                        modifier = Modifier.sizeIn(minHeight = layoutSpec.buttonMinHeight - 4.dp),
                    )
                }
            }
            Text(
                text = strings.pdfExportStyleTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = strings.pdfExportStyleDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                modifier = Modifier.testTag("pdf-export-style-group"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PdfExportStyleUiState.entries.forEach { option ->
                    FilterChip(
                        selected = state.pdfExportStyle == option,
                        onClick = { onPdfExportStyleChanged(option) },
                        label = {
                            Text(
                                text = option.label(strings),
                                maxLines = 2,
                            )
                        },
                        modifier = Modifier.sizeIn(minHeight = layoutSpec.buttonMinHeight - 4.dp),
                    )
                }
            }
            Text(
                text = state.pdfExportStyle.description(strings),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = strings.brandingTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = strings.brandingDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    BrandingLogoPreview(
                        brandingLogoBase64 = state.brandingLogoBase64,
                        contentDescription = strings.brandingLogoContentDescription(strings.settingsTitle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 84.dp)
                            .testTag("branding-logo-preview"),
                        showPlaceholderWhenEmpty = true,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AppActionButton(
                            text = if (state.hasBrandingLogo) {
                                strings.replaceBrandingLogoLabel
                            } else {
                                strings.uploadBrandingLogoLabel
                            },
                            onClick = {
                                brandLogoPickerLauncher.pickImage { bytes ->
                                    bytes?.let(onPickBrandingLogo)
                                }
                            },
                            variant = AppButtonVariant.SECONDARY,
                            minHeight = layoutSpec.buttonMinHeight,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("branding-logo-upload-button"),
                        )
                        AppActionButton(
                            text = strings.removeBrandingLogoLabel,
                            onClick = onClearBrandingLogo,
                            enabled = state.hasBrandingLogo,
                            variant = AppButtonVariant.TERTIARY,
                            minHeight = layoutSpec.buttonMinHeight,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("branding-logo-remove-button"),
                        )
                    }
                }
            }
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = strings.privacyTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = strings.privacyDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = strings.privacyBody,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun AccessibilityOptionRow(
    title: String,
    description: String,
    checked: Boolean,
    layoutSpec: AccessibilityLayoutSpec,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(22.dp))
            .background(
                if (checked) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.78f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                },
            )
            .toggleable(
                value = checked,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            )
            .heightIn(min = layoutSpec.optionMinHeight)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = null,
        )
    }
}

private fun PdfExportStyleUiState.label(strings: AppStrings): String = strings.pdfExportStyleLabel(toDomain())

private fun PdfExportStyleUiState.description(strings: AppStrings): String = strings.pdfExportStyleOptionDescription(toDomain())

private fun PdfExportStyleUiState.toDomain(): PdfExportStyle = when (this) {
    PdfExportStyleUiState.RETRO -> PdfExportStyle.RETRO
    PdfExportStyleUiState.SIMPLE_TABLE -> PdfExportStyle.SIMPLE_TABLE
    PdfExportStyleUiState.COMPACT_LIST -> PdfExportStyle.COMPACT_LIST
    PdfExportStyleUiState.DETAIL_BLOCKS -> PdfExportStyle.DETAIL_BLOCKS
}
