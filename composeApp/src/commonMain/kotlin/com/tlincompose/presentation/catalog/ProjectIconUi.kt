package com.tlincompose.presentation.catalog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ProjectIconPreset
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun ProjectIconPreset.imageVector(): ImageVector = when (this) {
    ProjectIconPreset.WORK -> Icons.Filled.Work
    ProjectIconPreset.CODE -> Icons.Filled.Code
    ProjectIconPreset.PALETTE -> Icons.Filled.Palette
    ProjectIconPreset.BUILD -> Icons.Filled.Build
    ProjectIconPreset.BUG_REPORT -> Icons.Filled.BugReport
    ProjectIconPreset.FOLDER -> Icons.Filled.Folder
    ProjectIconPreset.SETTINGS -> Icons.Filled.Settings
    ProjectIconPreset.SCHOOL -> Icons.Filled.School
}

fun EntryType.defaultImageVector(): ImageVector? = when (this) {
    EntryType.PROJECT -> null
    EntryType.COURSE -> Icons.Filled.School
    EntryType.VACATION -> Icons.Filled.BeachAccess
    EntryType.PERMIT -> Icons.Filled.Schedule
}

@OptIn(ExperimentalEncodingApi::class)
fun decodeProjectCustomIcon(base64: String?): ImageBitmap? {
    if (base64.isNullOrBlank()) return null
    return runCatching {
        Base64.Default.decode(base64).decodeToImageBitmap()
    }.getOrNull()
}
