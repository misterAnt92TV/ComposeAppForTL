package com.tlincompose.presentation

import androidx.compose.runtime.staticCompositionLocalOf
import com.tlincompose.core.AppStrings
import com.tlincompose.core.appStrings
import com.tlincompose.domain.model.AppLanguage

internal val LocalAppStrings = staticCompositionLocalOf<AppStrings> {
    appStrings(AppLanguage.ENGLISH)
}
