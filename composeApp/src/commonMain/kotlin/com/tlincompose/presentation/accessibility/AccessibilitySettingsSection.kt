package com.tlincompose.presentation.accessibility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.tlincompose.core.exportEmployeeIdLabel
import com.tlincompose.core.exportEmployeeIdPlaceholder
import com.tlincompose.core.exportMetadataDescription
import com.tlincompose.core.exportMetadataTitle
import com.tlincompose.core.exportOfficeNameLabel
import com.tlincompose.core.exportOfficeNamePlaceholder
import com.tlincompose.core.exportPersonIdLabel
import com.tlincompose.core.exportPersonIdPlaceholder
import com.tlincompose.core.exportUserFullNameLabel
import com.tlincompose.core.exportUserFullNamePlaceholder
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
import com.tlincompose.core.thirdPartyLibrariesDescription
import com.tlincompose.core.thirdPartyLibrariesTitle
import com.tlincompose.core.thirdPartyLibraryContentDescription
import com.tlincompose.core.uploadBrandingLogoLabel
import com.tlincompose.core.workdayHoursDescription
import com.tlincompose.core.workdayHoursTitle
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.PdfExportStyle
import com.tlincompose.presentation.BrandLogoPickerLauncher
import com.tlincompose.presentation.JsonFilePickerLauncher
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
    onExportUserFullNameChanged: (String) -> Unit,
    onExportOfficeNameChanged: (String) -> Unit,
    onExportEmployeeIdChanged: (String) -> Unit,
    onExportPersonIdChanged: (String) -> Unit,
    onPdfExportStyleChanged: (PdfExportStyleUiState) -> Unit,
    onPickBrandingLogo: (ByteArray) -> Unit,
    onClearBrandingLogo: () -> Unit,
    settingsBackupState: SettingsBackupUiState,
    jsonFilePickerLauncher: JsonFilePickerLauncher,
    onExportBackup: () -> Unit,
    onImportBackupSelected: (com.tlincompose.presentation.JsonFileSelection?) -> Unit,
    onConfirmImport: () -> Unit,
    onDismissImportConfirmation: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isCompactWidth = maxWidth < layoutSpec.compactBreakpoint
        val isNarrowWidth = maxWidth < layoutSpec.compactBreakpoint
        val isTwoColumnWidth = maxWidth >= layoutSpec.twoColumnBreakpoint
        val chipMinWidth = 136.dp
        val chipMaxWidth = 208.dp
        val closeButtonModifier = Modifier.widthIn(min = 96.dp, max = 128.dp)
        val brandingActionModifier = Modifier.widthIn(min = 160.dp, max = 220.dp)

        @Composable
        fun ThemeSection() {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
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
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2,
                                    textAlign = TextAlign.Center,
                                )
                            },
                            modifier = Modifier.sizeIn(
                                minWidth = chipMinWidth,
                                maxWidth = chipMaxWidth,
                                minHeight = layoutSpec.buttonMinHeight - 4.dp,
                            ),
                        )
                    }
                }
                Text(
                    text = state.themeMode.description(strings),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        @Composable
        fun TextScaleSection() {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = strings.textSizeTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
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
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2,
                                    textAlign = TextAlign.Center,
                                )
                            },
                            modifier = Modifier.sizeIn(
                                minWidth = chipMinWidth,
                                maxWidth = chipMaxWidth,
                                minHeight = layoutSpec.buttonMinHeight - 4.dp,
                            ),
                        )
                    }
                }
                Text(
                    text = state.textScale.description(strings),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        @Composable
        fun StandardWorkdaySection() {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
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
                    if (isCompactWidth) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
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
                                minWidth = layoutSpec.buttonMinWidth,
                                modifier = Modifier
                                    .weight(1f)
                                    .semantics {
                                        contentDescription = strings.decreaseWorkdayHours
                                    },
                            )
                            AppActionButton(
                                text = strings.increaseWorkdayButtonLabel,
                                onClick = {
                                    onStandardWorkdayChanged((state.standardWorkdayMinutes + 30).coerceAtMost(16 * 60))
                                },
                                variant = AppButtonVariant.SECONDARY,
                                minHeight = layoutSpec.buttonMinHeight,
                                minWidth = layoutSpec.buttonMinWidth,
                                modifier = Modifier
                                    .weight(1f)
                                    .semantics {
                                        contentDescription = strings.increaseWorkdayHours
                                    },
                            )
                        }
                    } else {
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
                                minWidth = layoutSpec.buttonMinWidth,
                                maxWidth = 128.dp,
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
                                minWidth = layoutSpec.buttonMinWidth,
                                maxWidth = 128.dp,
                                modifier = Modifier.semantics {
                                    contentDescription = strings.increaseWorkdayHours
                                },
                            )
                        }
                    }
                }
            }
        }

        @Composable
        fun AccessibilityTogglesSection() {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AccessibilityOptionRow(
                    title = strings.highContrastTitle,
                    description = strings.highContrastDescription,
                    checked = state.highContrast,
                    layoutSpec = layoutSpec,
                    stackTrailingControl = isNarrowWidth,
                    onCheckedChange = onHighContrastChanged,
                    modifier = Modifier.testTag("high-contrast-toggle"),
                )
                AccessibilityOptionRow(
                    title = strings.comfortableLayoutTitle,
                    description = strings.comfortableLayoutDescription,
                    checked = state.comfortableSpacing,
                    layoutSpec = layoutSpec,
                    stackTrailingControl = isNarrowWidth,
                    onCheckedChange = onComfortableSpacingChanged,
                    modifier = Modifier.testTag("comfortable-spacing-toggle"),
                )
                AccessibilityOptionRow(
                    title = strings.focusModeTitle,
                    description = strings.focusModeDescription,
                    checked = state.focusMode,
                    layoutSpec = layoutSpec,
                    stackTrailingControl = isNarrowWidth,
                    onCheckedChange = onFocusModeChanged,
                    modifier = Modifier.testTag("focus-mode-toggle"),
                )
            }
        }

        @Composable
        fun LanguageSection() {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
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
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2,
                                    textAlign = TextAlign.Center,
                                )
                            },
                            modifier = Modifier.sizeIn(
                                minWidth = chipMinWidth,
                                maxWidth = chipMaxWidth,
                                minHeight = layoutSpec.buttonMinHeight - 4.dp,
                            ),
                        )
                    }
                }
            }
        }

        @Composable
        fun ExportMetadataSection() {
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
                        text = strings.exportMetadataTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = strings.exportMetadataDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedTextField(
                        value = state.exportUserFullName,
                        onValueChange = onExportUserFullNameChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export-user-full-name-field"),
                        label = { Text(strings.exportUserFullNameLabel) },
                        placeholder = { Text(strings.exportUserFullNamePlaceholder) },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = state.exportOfficeName,
                        onValueChange = onExportOfficeNameChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export-office-name-field"),
                        label = { Text(strings.exportOfficeNameLabel) },
                        placeholder = { Text(strings.exportOfficeNamePlaceholder) },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = state.exportEmployeeId,
                        onValueChange = onExportEmployeeIdChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export-employee-id-field"),
                        label = { Text(strings.exportEmployeeIdLabel) },
                        placeholder = { Text(strings.exportEmployeeIdPlaceholder) },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = state.exportPersonId,
                        onValueChange = onExportPersonIdChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export-person-id-field"),
                        label = { Text(strings.exportPersonIdLabel) },
                        placeholder = { Text(strings.exportPersonIdPlaceholder) },
                        singleLine = true,
                    )
                }
            }
        }

        @Composable
        fun PdfExportStyleSection() {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
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
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2,
                                    textAlign = TextAlign.Center,
                                )
                            },
                            modifier = Modifier.sizeIn(
                                minWidth = chipMinWidth,
                                maxWidth = chipMaxWidth,
                                minHeight = layoutSpec.buttonMinHeight - 4.dp,
                            ),
                        )
                    }
                }
                Text(
                    text = state.pdfExportStyle.description(strings),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        @Composable
        fun BrandingSection() {
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
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
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
                            minWidth = layoutSpec.buttonPreferredWidth,
                            maxWidth = 220.dp,
                            modifier = brandingActionModifier.testTag("branding-logo-upload-button"),
                        )
                        AppActionButton(
                            text = strings.removeBrandingLogoLabel,
                            onClick = onClearBrandingLogo,
                            enabled = state.hasBrandingLogo,
                            variant = AppButtonVariant.TERTIARY,
                            minHeight = layoutSpec.buttonMinHeight,
                            minWidth = 160.dp,
                            maxWidth = 220.dp,
                            modifier = brandingActionModifier.testTag("branding-logo-remove-button"),
                        )
                    }
                }
            }
        }

        @Composable
        fun ThirdPartyLibrariesSection() {
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.testTag("third-party-libraries-section"),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = strings.thirdPartyLibrariesTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = strings.thirdPartyLibrariesDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        thirdPartyLibrariesList().forEach { library ->
                            ThirdPartyLibraryRow(library, strings)
                        }
                    }
                }
            }
        }

        @Composable
        fun PrivacySection() {
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

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("accessibility-settings-section"),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(layoutSpec.cardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = layoutSpec.panelMaxWidth)
                        .padding(layoutSpec.contentPadding)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
                    verticalArrangement = Arrangement.spacedBy(layoutSpec.sectionSpacing),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            AppActionButton(
                                text = strings.closeLabel,
                                onClick = onClose,
                                variant = AppButtonVariant.TERTIARY,
                                minHeight = layoutSpec.buttonMinHeight,
                                minWidth = 96.dp,
                                maxWidth = 128.dp,
                                modifier = closeButtonModifier.testTag("settings-close-button"),
                            )
                        }
                    }

                    if (isTwoColumnWidth) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(layoutSpec.sectionSpacing),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(layoutSpec.sectionSpacing),
                            ) {
                                ThemeSection()
                                TextScaleSection()
                                StandardWorkdaySection()
                                AccessibilityTogglesSection()
                                LanguageSection()
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(layoutSpec.sectionSpacing),
                            ) {
                                ExportMetadataSection()
                                PdfExportStyleSection()
                                BrandingSection()
                                SettingsBackupSection(
                                    state = settingsBackupState,
                                    layoutSpec = layoutSpec,
                                    jsonFilePickerLauncher = jsonFilePickerLauncher,
                                    onExportBackup = onExportBackup,
                                    onImportBackupSelected = onImportBackupSelected,
                                    onConfirmImport = onConfirmImport,
                                    onDismissImportConfirmation = onDismissImportConfirmation,
                                )
                                ThirdPartyLibrariesSection()
                                PrivacySection()
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(layoutSpec.sectionSpacing),
                        ) {
                            ThemeSection()
                            TextScaleSection()
                            StandardWorkdaySection()
                            AccessibilityTogglesSection()
                            LanguageSection()
                            ExportMetadataSection()
                            PdfExportStyleSection()
                            BrandingSection()
                            SettingsBackupSection(
                                state = settingsBackupState,
                                layoutSpec = layoutSpec,
                                jsonFilePickerLauncher = jsonFilePickerLauncher,
                                onExportBackup = onExportBackup,
                                onImportBackupSelected = onImportBackupSelected,
                                onConfirmImport = onConfirmImport,
                                onDismissImportConfirmation = onDismissImportConfirmation,
                            )
                            ThirdPartyLibrariesSection()
                            PrivacySection()
                        }
                    }
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
    stackTrailingControl: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (stackTrailingControl) {
        Column(
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Switch(
                    checked = checked,
                    onCheckedChange = null,
                )
            }
        }
        return
    }

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

@Composable
private fun ThirdPartyLibraryRow(
    library: ThirdPartyLibraryUi,
    strings: AppStrings,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        val iconVector = when (library.icon) {
            ThirdPartyLibraryIcon.COMPOSE -> androidx.compose.material.icons.Icons.Outlined.Brush
            ThirdPartyLibraryIcon.KOTLIN -> androidx.compose.material.icons.Icons.Outlined.Code
            ThirdPartyLibraryIcon.KOIN -> androidx.compose.material.icons.Icons.Outlined.Extension
            ThirdPartyLibraryIcon.KERMIT -> androidx.compose.material.icons.Icons.Outlined.BugReport
            ThirdPartyLibraryIcon.ANDROIDSVG -> androidx.compose.material.icons.Icons.Outlined.Image
            ThirdPartyLibraryIcon.BATIK -> androidx.compose.material.icons.Icons.Outlined.Image
            ThirdPartyLibraryIcon.ANDROIDX -> androidx.compose.material.icons.Icons.Outlined.Android
            ThirdPartyLibraryIcon.OTHER -> androidx.compose.material.icons.Icons.Outlined.Book
        }
        androidx.compose.material3.Icon(
            imageVector = iconVector,
            contentDescription = strings.thirdPartyLibraryContentDescription(library.name),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(32.dp)
                .padding(top = 2.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = library.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = strings[com.tlincompose.core.StringKey.ThirdPartyLibraryVersion(library.version)],
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
            Text(
                text = library.websiteUrl,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            library.module?.let { moduleName ->
                Text(
                    text = moduleName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// --- Third party libraries UI models ---

private enum class ThirdPartyLibraryIcon {
    COMPOSE, KOTLIN, KOIN, KERMIT, ANDROIDSVG, BATIK, ANDROIDX, OTHER
}

private data class ThirdPartyLibraryUi(
    val name: String,
    val version: String,
    val websiteUrl: String,
    val icon: ThirdPartyLibraryIcon,
    val module: String? = null
)

private fun thirdPartyLibrariesList(): List<ThirdPartyLibraryUi> = listOf(
    ThirdPartyLibraryUi(
        name = "JetBrains Compose Multiplatform",
        version = "1.10.3",
        websiteUrl = "https://www.jetbrains.com/lp/compose-mpp/",
        icon = ThirdPartyLibraryIcon.COMPOSE,
        module = "org.jetbrains.compose.*"
    ),
    ThirdPartyLibraryUi(
        name = "Kotlin Coroutines (kotlinx-coroutines-core)",
        version = "1.10.2",
        websiteUrl = "https://github.com/Kotlin/kotlinx.coroutines",
        icon = ThirdPartyLibraryIcon.KOTLIN,
        module = "org.jetbrains.kotlinx:kotlinx-coroutines-core"
    ),
    ThirdPartyLibraryUi(
        name = "KotlinX Datetime",
        version = "0.7.1",
        websiteUrl = "https://github.com/Kotlin/kotlinx-datetime",
        icon = ThirdPartyLibraryIcon.KOTLIN,
        module = "org.jetbrains.kotlinx:kotlinx-datetime"
    ),
    ThirdPartyLibraryUi(
        name = "KotlinX Serialization",
        version = "1.9.0",
        websiteUrl = "https://github.com/Kotlin/kotlinx.serialization",
        icon = ThirdPartyLibraryIcon.KOTLIN,
        module = "org.jetbrains.kotlinx:kotlinx-serialization-json"
    ),
    ThirdPartyLibraryUi(
        name = "Koin (DI)",
        version = "4.1.1",
        websiteUrl = "https://insert-koin.io/",
        icon = ThirdPartyLibraryIcon.KOIN,
        module = "io.insert-koin:koin-core"
    ),
    ThirdPartyLibraryUi(
        name = "Kermit Logging",
        version = "2.1.0",
        websiteUrl = "https://github.com/touchlab/Kermit",
        icon = ThirdPartyLibraryIcon.KERMIT,
        module = "co.touchlab:kermit"
    ),
    ThirdPartyLibraryUi(
        name = "AndroidSVG",
        version = "1.4",
        websiteUrl = "https://bigbadaboom.github.io/androidsvg/",
        icon = ThirdPartyLibraryIcon.ANDROIDSVG,
        module = "com.caverock:androidsvg-aar"
    ),
    ThirdPartyLibraryUi(
        name = "Apache Batik Transcoder",
        version = "1.17",
        websiteUrl = "https://xmlgraphics.apache.org/batik/",
        icon = ThirdPartyLibraryIcon.BATIK,
        module = "org.apache.xmlgraphics:batik-transcoder"
    ),
    ThirdPartyLibraryUi(
        name = "AndroidX Activity Compose",
        version = "1.10.1",
        websiteUrl = "https://developer.android.com/jetpack/androidx/releases/activity",
        icon = ThirdPartyLibraryIcon.ANDROIDX,
        module = "androidx.activity:activity-compose"
    )
)
