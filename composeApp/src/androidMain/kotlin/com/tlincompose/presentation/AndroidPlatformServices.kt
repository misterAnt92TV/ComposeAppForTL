package com.tlincompose.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.caverock.androidsvg.SVG
import com.tlincompose.core.BrandingLogoSize
import com.tlincompose.core.BrandingRectangleLogoMaxHeightPx
import com.tlincompose.core.BrandingRectangleLogoMaxWidthPx
import com.tlincompose.core.MaxBrandingLogoBytes
import com.tlincompose.core.appStrings
import com.tlincompose.core.brandingLogoTargetSizeFor
import com.tlincompose.core.computeBrandingLogoRenderSize
import com.tlincompose.core.isSvgImageBytes
import com.tlincompose.core.parseSvgViewportSize
import com.tlincompose.core.unableToReadSelectedFile
import com.tlincompose.core.unableToReadSelectedImage
import com.tlincompose.data.export.PdfFontProvider
import com.tlincompose.data.export.PdfFontResource
import com.tlincompose.data.local.StorageDriver
import com.tlincompose.domain.model.AppLanguage
import java.io.ByteArrayOutputStream
import java.io.File

@Composable
actual fun rememberPlatformServices(
    language: AppLanguage,
    onMessage: (String) -> Unit,
): PlatformServices {
    val strings = appStrings(language)
    val context = LocalContext.current.applicationContext
    val storageDriver = remember(context) { AndroidStorageDriver(context) }
    val pdfFontBytes = remember(context) {
        context.assets.open("fonts/arial.ttf").use { it.readBytes() }
    }
    val pdfFontProvider = remember(pdfFontBytes) {
        object : PdfFontProvider {
            override fun loadRegularFont(): PdfFontResource = PdfFontResource(
                postScriptName = "ArialMT",
                fontBytes = pdfFontBytes,
            )
        }
    }
    var pendingDocument by remember { mutableStateOf<com.tlincompose.domain.model.ExportDocument?>(null) }
    var pendingIconPick by remember { mutableStateOf<((ByteArray?) -> Unit)?>(null) }
    var pendingBrandLogoPick by remember { mutableStateOf<((ByteArray?) -> Unit)?>(null) }
    var pendingJsonFilePick by remember { mutableStateOf<((JsonFileSelection?) -> Unit)?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val document = pendingDocument
        pendingDocument = null
        if (document == null) return@rememberLauncherForActivityResult

        if (result.resultCode != Activity.RESULT_OK) {
            onMessage(strings[com.tlincompose.core.StringKey.SaveCancelledMessage])
            return@rememberLauncherForActivityResult
        }

        val destination = result.data?.data
        if (destination == null) {
            onMessage(strings[com.tlincompose.core.StringKey.InvalidDestinationMessage])
            return@rememberLauncherForActivityResult
        }

        runCatching {
            context.contentResolver.openOutputStream(destination)?.use { output ->
                output.write(document.bytes)
            } ?: error(strings[com.tlincompose.core.StringKey.UnableToOpenDestinationFile])
        }.onSuccess {
            onMessage(strings[com.tlincompose.core.StringKey.FileSavedMessage(document.fileName)])
        }.onFailure {
            onMessage(strings[com.tlincompose.core.StringKey.SaveErrorMessage])
        }
    }
    val iconPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val callback = pendingIconPick
        pendingIconPick = null
        if (callback == null) return@rememberLauncherForActivityResult
        if (uri == null) {
            callback(null)
            return@rememberLauncherForActivityResult
        }

        runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                input.readBytes()
            } ?: error(strings[com.tlincompose.core.StringKey.UnableToReadSelectedImage])
        }.onSuccess {
            callback(it)
        }.onFailure {
            onMessage(strings[com.tlincompose.core.StringKey.UnableToReadSelectedImage])
            callback(null)
        }
    }
    val brandLogoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val callback = pendingBrandLogoPick
        pendingBrandLogoPick = null
        if (callback == null) return@rememberLauncherForActivityResult
        if (uri == null) {
            callback(null)
            return@rememberLauncherForActivityResult
        }

        runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                input.readBytes()
            } ?: error(strings.unableToReadSelectedImage)
        }.onSuccess { rawBytes ->
            callback(normalizeBrandingLogoBytes(rawBytes) ?: byteArrayOf())
        }.onFailure {
            onMessage(strings.unableToReadSelectedImage)
            callback(null)
        }
    }
    val jsonFilePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val callback = pendingJsonFilePick
        pendingJsonFilePick = null
        if (callback == null) return@rememberLauncherForActivityResult
        if (uri == null) {
            callback(null)
            return@rememberLauncherForActivityResult
        }

        runCatching {
            val bytes = context.contentResolver.openInputStream(uri)?.use { input ->
                input.readBytes()
            } ?: error(strings.unableToReadSelectedFile)
            JsonFileSelection(
                fileName = context.resolveDisplayName(uri) ?: "TLInCompose_backup.json",
                bytes = bytes,
            )
        }.onSuccess {
            callback(it)
        }.onFailure {
            onMessage(strings.unableToReadSelectedFile)
            callback(null)
        }
    }

    val fileSaveLauncher = remember(context, launcher, strings) {
        object : FileSaveLauncher {
            override fun save(document: com.tlincompose.domain.model.ExportDocument) {
                pendingDocument = document
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = document.mimeType
                    putExtra(Intent.EXTRA_TITLE, document.fileName)
                }
                launcher.launch(intent)
            }
        }
    }
    val projectIconPickerLauncher = remember(context, iconPickerLauncher) {
        object : ProjectIconPickerLauncher {
            override fun pickImage(onImagePicked: (ByteArray?) -> Unit) {
                pendingIconPick = onImagePicked
                iconPickerLauncher.launch(arrayOf("image/png", "image/jpeg", "image/webp"))
            }
        }
    }
    val appBrandLogoPickerLauncher = remember(context, brandLogoPickerLauncher) {
        object : BrandLogoPickerLauncher {
            override fun pickImage(onImagePicked: (ByteArray?) -> Unit) {
                pendingBrandLogoPick = onImagePicked
                brandLogoPickerLauncher.launch(arrayOf("image/png", "image/jpeg", "image/svg+xml"))
            }
        }
    }
    val jsonFilePickerLauncher = remember(context, jsonFilePicker) {
        object : JsonFilePickerLauncher {
            override fun pickFile(onFilePicked: (JsonFileSelection?) -> Unit) {
                pendingJsonFilePick = onFilePicked
                jsonFilePicker.launch(arrayOf("application/json"))
            }
        }
    }

    return PlatformServices(
        storageDriver = storageDriver,
        pdfFontProvider = pdfFontProvider,
        fileSaveLauncher = fileSaveLauncher,
        projectIconPickerLauncher = projectIconPickerLauncher,
        brandLogoPickerLauncher = appBrandLogoPickerLauncher,
        jsonFilePickerLauncher = jsonFilePickerLauncher,
    )
}

private fun Context.resolveDisplayName(uri: android.net.Uri): String? {
    return contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (!cursor.moveToFirst()) return@use null
        val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (columnIndex < 0) return@use null
        cursor.getString(columnIndex)
    }
}

private fun normalizeBrandingLogoBytes(rawBytes: ByteArray): ByteArray? {
    val sourceBitmap = if (isSvgImageBytes(rawBytes)) {
        renderSvgToBitmap(rawBytes) ?: return null
    } else {
        BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size) ?: return null
    }
    val targetSize = brandingLogoTargetSizeFor(sourceBitmap.width, sourceBitmap.height)
    val targetBitmap = Bitmap.createBitmap(
        targetSize.width,
        targetSize.height,
        Bitmap.Config.ARGB_8888,
    )
    val renderSize = computeBrandingLogoRenderSize(
        width = sourceBitmap.width,
        height = sourceBitmap.height,
        maxWidth = targetSize.width.toFloat(),
        maxHeight = targetSize.height.toFloat(),
    )
    Canvas(targetBitmap).apply {
        drawColor(Color.WHITE)
        val left = (targetSize.width - renderSize.width) / 2f
        val top = (targetSize.height - renderSize.height) / 2f
        drawBitmap(
            sourceBitmap,
            null,
            RectF(left, top, left + renderSize.width, top + renderSize.height),
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG),
        )
    }
    sourceBitmap.recycle()

    val qualities = listOf(90, 80, 70, 60)
    val normalizedBytes = qualities.firstNotNullOfOrNull { quality ->
        ByteArrayOutputStream().use { output ->
            if (!targetBitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)) return@use null
            output.toByteArray().takeIf { it.size <= MaxBrandingLogoBytes }
        }
    }
    targetBitmap.recycle()
    return normalizedBytes
}

private fun renderSvgToBitmap(rawBytes: ByteArray): Bitmap? {
    val svg = runCatching { SVG.getFromString(rawBytes.decodeToString()) }.getOrNull() ?: return null
    val sourceSize = parseSvgViewportSize(rawBytes)
        ?: BrandingLogoSize(
            width = BrandingRectangleLogoMaxWidthPx,
            height = BrandingRectangleLogoMaxHeightPx,
        )
    val targetSize = brandingLogoTargetSizeFor(sourceSize.width, sourceSize.height)
    val targetBitmap = Bitmap.createBitmap(
        targetSize.width,
        targetSize.height,
        Bitmap.Config.ARGB_8888,
    )
    val renderSize = computeBrandingLogoRenderSize(
        width = sourceSize.width,
        height = sourceSize.height,
        maxWidth = targetSize.width.toFloat(),
        maxHeight = targetSize.height.toFloat(),
    )
    Canvas(targetBitmap).apply {
        drawColor(Color.WHITE)
        save()
        translate(
            (targetSize.width - renderSize.width) / 2f,
            (targetSize.height - renderSize.height) / 2f,
        )
        svg.setDocumentWidth(renderSize.width.toFloat())
        svg.setDocumentHeight(renderSize.height.toFloat())
        svg.renderToCanvas(this)
        restore()
    }
    return targetBitmap
}

private class AndroidStorageDriver(private val context: Context) : StorageDriver {
    override fun read(fileName: String): String? {
        val file = fileFor(fileName)
        return if (file.exists()) file.readText(Charsets.UTF_8) else null
    }

    override fun write(fileName: String, content: String) {
        val file = fileFor(fileName)
        file.parentFile?.mkdirs()
        file.writeText(content, Charsets.UTF_8)
    }

    private fun fileFor(fileName: String): File = File(context.filesDir, fileName)
}
