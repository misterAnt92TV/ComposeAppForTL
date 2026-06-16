package com.tlincompose.presentation.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tlincompose.core.activityCatalogDescription
import com.tlincompose.core.activityCatalogTitle
import com.tlincompose.core.baseDuration
import com.tlincompose.core.closeLabel
import com.tlincompose.core.createdOn
import com.tlincompose.core.deleteLabel
import com.tlincompose.core.displayName
import com.tlincompose.core.editLabel
import com.tlincompose.core.formatHours
import com.tlincompose.core.newExtEntity
import com.tlincompose.core.noSavedEntities
import com.tlincompose.core.updatedOn
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.components.AppActionButton
import com.tlincompose.presentation.components.AppButtonVariant
import com.tlincompose.presentation.components.HeaderInfoPill
import com.tlincompose.presentation.components.StatusPill
import com.tlincompose.presentation.layout.AccessibilityLayoutSpec

@Composable
internal fun ActivityCatalogSection(
    definitions: List<ActivityDefinition>,
    layoutSpec: AccessibilityLayoutSpec,
    defaultWorkdayMinutes: Int,
    onCreateDefinition: () -> Unit,
    onEditDefinition: (ActivityDefinition) -> Unit,
    onDeleteDefinition: (ActivityDefinition) -> Unit,
    onClose: () -> Unit,
) {
    val strings = LocalAppStrings.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("activity-catalog-section"),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(layoutSpec.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(layoutSpec.contentPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp),
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
                        text = strings.activityCatalogTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = strings.activityCatalogDescription(formatHours(defaultWorkdayMinutes)),
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
                        modifier = Modifier.testTag("activity-catalog-close-button"),
                    )
                }
            }
            AppActionButton(
                text = strings.newExtEntity,
                onClick = onCreateDefinition,
                minHeight = layoutSpec.buttonMinHeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create-activity-definition-button"),
            )
            if (definitions.isEmpty()) {
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                ) {
                    Text(
                        text = strings.noSavedEntities,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                definitions.forEach { definition ->
                    ActivityDefinitionCard(
                        definition = definition,
                        layoutSpec = layoutSpec,
                        onEdit = { onEditDefinition(definition) },
                        onDelete = { onDeleteDefinition(definition) },
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun ActivityDefinitionCard(
    definition: ActivityDefinition,
    layoutSpec: AccessibilityLayoutSpec,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val strings = LocalAppStrings.current
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f),
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    ProjectIconAvatar(
                        entryType = definition.type,
                        projectIconPreset = definition.projectIconPreset,
                        projectCustomIconBase64 = definition.projectCustomIconBase64,
                        title = definition.title,
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = definition.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = definition.extCode,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                StatusPill(
                    text = definition.type.displayName(strings.language),
                    background = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            if (definition.description.isNotBlank()) {
                Text(
                    text = definition.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HeaderInfoPill(text = strings.baseDuration(formatHours(definition.defaultMinutes)))
                HeaderInfoPill(text = strings.createdOn(definition.createdDate))
                HeaderInfoPill(text = strings.updatedOn(definition.updatedDate))
            }
            definition.projectUrl?.let { projectUrl ->
                Text(
                    text = projectUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AppActionButton(
                    text = strings.editLabel,
                    onClick = onEdit,
                    variant = AppButtonVariant.SECONDARY,
                    minHeight = layoutSpec.buttonMinHeight,
                    modifier = Modifier.weight(1f),
                )
                AppActionButton(
                    text = strings.deleteLabel,
                    onClick = onDelete,
                    variant = AppButtonVariant.TERTIARY,
                    minHeight = layoutSpec.buttonMinHeight,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
