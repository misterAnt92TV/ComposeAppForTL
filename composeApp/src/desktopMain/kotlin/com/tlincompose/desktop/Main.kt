package com.tlincompose.desktop

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.tlincompose.core.AppStrings
import com.tlincompose.core.StringKey
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.presentation.App

fun main() = application {
    val strings = AppStrings(AppLanguage.ITALIAN)
    Window(
        icon = painterResource("icons/tlincompose-window.png"),
        onCloseRequest = ::exitApplication,
        title = strings[StringKey.AppName],
    ) {
        App()
    }
}
