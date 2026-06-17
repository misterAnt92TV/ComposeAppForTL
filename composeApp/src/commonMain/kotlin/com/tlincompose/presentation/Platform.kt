package com.tlincompose.presentation

import androidx.compose.runtime.Composable
import com.tlincompose.data.export.PdfFontProvider
import com.tlincompose.data.local.StorageDriver
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.ExportDocument

interface FileSaveLauncher {
    fun save(document: ExportDocument)
}

interface ProjectIconPickerLauncher {
    fun pickImage(onImagePicked: (ByteArray?) -> Unit)
}

interface BrandLogoPickerLauncher {
    fun pickImage(onImagePicked: (ByteArray?) -> Unit)
}

data class PlatformServices(
    val storageDriver: StorageDriver,
    val pdfFontProvider: PdfFontProvider,
    val fileSaveLauncher: FileSaveLauncher,
    val projectIconPickerLauncher: ProjectIconPickerLauncher,
    val brandLogoPickerLauncher: BrandLogoPickerLauncher,
)

expect @Composable
fun rememberPlatformServices(
    language: AppLanguage,
    onMessage: (String) -> Unit,
): PlatformServices
