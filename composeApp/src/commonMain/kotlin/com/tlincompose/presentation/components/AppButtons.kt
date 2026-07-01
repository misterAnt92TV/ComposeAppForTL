package com.tlincompose.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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

    val labelModifier = if (centerLabel) Modifier.fillMaxWidth() else Modifier
    val labelAlignment = if (centerLabel) TextAlign.Center else TextAlign.Start
    val content: @Composable () -> Unit = {
        Text(
            text = text,
            modifier = labelModifier,
            maxLines = 2,
            textAlign = labelAlignment,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
        )
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
