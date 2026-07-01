package com.tlincompose.presentation.layout

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tlincompose.presentation.accessibility.AccessibilitySettingsUiState
import com.tlincompose.presentation.accessibility.AccessibilityTextScaleUiState

internal data class AccessibilityLayoutSpec(
    val screenPadding: Dp,
    val sectionSpacing: Dp,
    val cardCornerRadius: Dp,
    val headerPadding: Dp,
    val contentPadding: Dp,
    val contentMaxWidth: Dp,
    val panelMaxWidth: Dp,
    val buttonMinHeight: Dp,
    val buttonMinWidth: Dp,
    val buttonPreferredWidth: Dp,
    val buttonMaxWidth: Dp,
    val dayCellMinHeight: Dp,
    val dayCellPadding: Dp,
    val optionMinHeight: Dp,
    val dialogMaxHeight: Dp,
    val compactBreakpoint: Dp,
    val wrapActionsBreakpoint: Dp,
    val twoColumnBreakpoint: Dp,
    val visibleSummaryCount: Int,
    val showSupportingCopy: Boolean,
    val showExtendedHeaderCopy: Boolean,
)

internal fun accessibilityLayoutSpec(state: AccessibilitySettingsUiState): AccessibilityLayoutSpec {
    val comfortableSpacing = state.comfortableSpacing
    return AccessibilityLayoutSpec(
        screenPadding = if (comfortableSpacing) 20.dp else 16.dp,
        sectionSpacing = if (comfortableSpacing) 20.dp else 16.dp,
        cardCornerRadius = if (comfortableSpacing) 30.dp else 24.dp,
        headerPadding = if (comfortableSpacing) 24.dp else 18.dp,
        contentPadding = if (comfortableSpacing) 18.dp else 14.dp,
        contentMaxWidth = 1180.dp,
        panelMaxWidth = 1040.dp,
        buttonMinHeight = if (comfortableSpacing) 52.dp else 48.dp,
        buttonMinWidth = 112.dp,
        buttonPreferredWidth = 168.dp,
        buttonMaxWidth = 240.dp,
        dayCellMinHeight = when {
            state.textScale == AccessibilityTextScaleUiState.EXTRA_LARGE && comfortableSpacing -> 172.dp
            state.textScale == AccessibilityTextScaleUiState.EXTRA_LARGE -> 144.dp
            comfortableSpacing -> 152.dp
            else -> 130.dp
        },
        dayCellPadding = if (comfortableSpacing) 12.dp else 10.dp,
        optionMinHeight = if (comfortableSpacing) 88.dp else 76.dp,
        dialogMaxHeight = if (comfortableSpacing) 520.dp else 440.dp,
        compactBreakpoint = 560.dp,
        wrapActionsBreakpoint = 720.dp,
        twoColumnBreakpoint = 960.dp,
        visibleSummaryCount = if (
            state.focusMode || state.textScale == AccessibilityTextScaleUiState.EXTRA_LARGE
        ) {
            2
        } else {
            3
        },
        showSupportingCopy = !state.focusMode,
        showExtendedHeaderCopy = !state.focusMode &&
            state.textScale != AccessibilityTextScaleUiState.EXTRA_LARGE,
    )
}
