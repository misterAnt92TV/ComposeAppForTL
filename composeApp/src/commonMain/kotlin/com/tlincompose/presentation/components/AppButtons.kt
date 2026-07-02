package com.tlincompose.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Dp.Companion.Unspecified
import androidx.compose.ui.unit.dp

internal enum class AppButtonVariant {
    PRIMARY,
    SECONDARY,
    TERTIARY,
}

@Composable
internal fun AppActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = 48.dp,
    minWidth: Dp = Unspecified,
    maxWidth: Dp = Unspecified,
    enabled: Boolean = true,
    variant: AppButtonVariant = AppButtonVariant.PRIMARY,
    centerLabel: Boolean = true,
    leadingIcon: ImageVector? = null,
    testTag: String? = null,
) {
    val shapedModifier = modifier
        .sizeIn(
            minWidth = minWidth,
            minHeight = minHeight,
            maxWidth = maxWidth,
        )
        .let { base ->
            if (testTag == null) {
                base
            } else {
                base.testTag(testTag)
            }
        }

    val content: @Composable () -> Unit = {
        Row(
            modifier = if (centerLabel) Modifier.fillMaxWidth() else Modifier,
            horizontalArrangement = if (centerLabel) Arrangement.Center else Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                maxLines = 2,
                textAlign = if (centerLabel) TextAlign.Center else TextAlign.Start,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
            )
        }
    }

    when (variant) {
        AppButtonVariant.PRIMARY -> Button(
            onClick = onClick,
            modifier = shapedModifier,
            enabled = enabled,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            contentPadding = AppButtonContentPadding,
        ) {
            content()
        }

        AppButtonVariant.SECONDARY -> OutlinedButton(
            onClick = onClick,
            modifier = shapedModifier,
            enabled = enabled,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            contentPadding = AppButtonContentPadding,
        ) {
            content()
        }

        AppButtonVariant.TERTIARY -> TextButton(
            onClick = onClick,
            modifier = shapedModifier,
            enabled = enabled,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            contentPadding = AppButtonContentPadding,
        ) {
            content()
        }
    }
}

private val AppButtonContentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
