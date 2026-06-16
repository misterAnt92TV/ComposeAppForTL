package com.tlincompose.presentation.layout

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import com.tlincompose.presentation.accessibility.AccessibilitySettingsUiState

@Composable
internal fun appBackgroundBrush(state: AccessibilitySettingsUiState): Brush {
    val colorScheme = MaterialTheme.colorScheme
    return remember(
        colorScheme.background,
        colorScheme.primaryContainer,
        colorScheme.secondaryContainer,
        colorScheme.surface,
        state.highContrast,
        state.focusMode,
    ) {
        if (state.highContrast || state.focusMode) {
            Brush.verticalGradient(
                colors = listOf(
                    colorScheme.background,
                    colorScheme.surface,
                    colorScheme.background,
                ),
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    colorScheme.background,
                    colorScheme.primaryContainer.copy(alpha = 0.55f),
                    colorScheme.secondaryContainer.copy(alpha = 0.28f),
                    colorScheme.background,
                ),
            )
        }
    }
}
