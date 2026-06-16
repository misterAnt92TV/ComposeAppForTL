package com.tlincompose.desktop

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.tlincompose.core.appName
import com.tlincompose.core.appStrings
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.presentation.App

fun main() = application {
    Window(
        icon = painterResource("icons/tlincompose-window.png"),
        onCloseRequest = ::exitApplication,
        title = appStrings(AppLanguage.ITALIAN).appName,
    ) {
        App()
    }
}
