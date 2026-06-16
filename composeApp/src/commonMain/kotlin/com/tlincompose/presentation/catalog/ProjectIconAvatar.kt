package com.tlincompose.presentation.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tlincompose.core.*
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ProjectIconPreset
import com.tlincompose.presentation.LocalAppStrings

@Composable
internal fun ProjectIconAvatar(
    entryType: EntryType = EntryType.PROJECT,
    projectIconPreset: ProjectIconPreset?,
    projectCustomIconBase64: String?,
    title: String,
    size: Dp = 48.dp,
    showPlaceholderWhenEmpty: Boolean = false,
) {
    val strings = LocalAppStrings.current
    val customIcon = remember(projectCustomIconBase64) {
        decodeProjectCustomIcon(projectCustomIconBase64)
    }
    val fallbackTypeIcon = entryType.defaultImageVector()
    val hasIcon = projectIconPreset != null || customIcon != null || fallbackTypeIcon != null
    if (!hasIcon && !showPlaceholderWhenEmpty) return

    val containerColor = when {
        customIcon != null || projectIconPreset != null -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
        entryType == EntryType.COURSE -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.78f)
        entryType == EntryType.VACATION -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.78f)
        entryType == EntryType.PERMIT -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.78f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    }
    val iconTint = when (entryType) {
        EntryType.PROJECT -> MaterialTheme.colorScheme.onPrimaryContainer
        EntryType.COURSE -> MaterialTheme.colorScheme.onErrorContainer
        EntryType.VACATION -> MaterialTheme.colorScheme.onTertiaryContainer
        EntryType.PERMIT -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(if (hasIcon) 16.dp else 18.dp),
        color = containerColor,
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center,
        ) {
            when {
                customIcon != null -> {
                    Image(
                        bitmap = customIcon,
                        contentDescription = strings.projectIconContentDescription(title),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                androidx.compose.foundation.shape.RoundedCornerShape(
                                    if (size > 40.dp) 16.dp else 12.dp,
                                ),
                            ),
                        contentScale = ContentScale.Crop,
                    )
                }

                projectIconPreset != null -> {
                    Icon(
                        imageVector = projectIconPreset.imageVector(),
                        contentDescription = strings.projectPresetContentDescription(
                            projectIconPreset.displayLabel(strings.language),
                        ),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(size * 0.48f),
                    )
                }

                fallbackTypeIcon != null -> {
                    Icon(
                        imageVector = fallbackTypeIcon,
                        contentDescription = strings.activityTypeIconContentDescription(
                            entryType.displayName(strings.language),
                        ),
                        tint = iconTint,
                        modifier = Modifier.size(size * 0.5f),
                    )
                }

                else -> {
                    Text(
                        text = title.take(1).ifBlank { "?" }.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
