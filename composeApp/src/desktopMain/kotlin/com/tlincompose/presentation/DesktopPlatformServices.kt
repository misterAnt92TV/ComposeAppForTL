package com.tlincompose.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.tlincompose.core.AppStrings
import com.tlincompose.core.BrandingLogoSize
import com.tlincompose.core.BrandingRectangleLogoMaxHeightPx
import com.tlincompose.core.BrandingRectangleLogoMaxWidthPx
import com.tlincompose.core.MaxBrandingLogoBytes
import com.tlincompose.core.appFileLabel
import com.tlincompose.core.appStrings
import com.tlincompose.core.brandingLogoTargetSizeFor
import com.tlincompose.core.chooseBrandLogoDialogTitle
import com.tlincompose.core.chooseProjectIconDialogTitle
import com.tlincompose.core.computeBrandingLogoRenderSize
import com.tlincompose.core.exportFormatLabel
import com.tlincompose.core.exportSaveDialogTitle
import com.tlincompose.core.fileSavedMessage
import com.tlincompose.core.imageFilesLabel
import com.tlincompose.core.isSvgImageBytes
import com.tlincompose.core.parseSvgViewportSize
import com.tlincompose.core.saveCancelledMessage
import com.tlincompose.core.saveErrorMessage
import com.tlincompose.core.unableToReadSelectedImage
import com.tlincompose.data.local.StorageDriver
import com.tlincompose.domain.model.AppLanguage
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
actual fun rememberPlatformServices(
    language: AppLanguage,
    onMessage: (String) -> Unit,
): PlatformServices {
    val strings = appStrings(language)
    val storageDriver = remember { DesktopStorageDriver() }
    val fileSaveLauncher = remember(onMessage, strings) {
        object : FileSaveLauncher {
            override fun save(document: com.tlincompose.domain.model.ExportDocument) {
                val chooser = JFileChooser().apply {
                    dialogTitle = strings.exportSaveDialogTitle
                    selectedFile = java.io.File(document.fileName)
                    fileFilter = FileNameExtensionFilter(
                        document.labelForChooser(strings),
                        document.extensionForChooser(),
                    )
                }
                val result = chooser.showSaveDialog(null)
                if (result != JFileChooser.APPROVE_OPTION) {
                    onMessage(strings.saveCancelledMessage)
                    return
                }
                runCatching {
                    chooser.selectedFile.writeBytes(document.bytes)
                }.onSuccess {
                    onMessage(strings.fileSavedMessage(document.fileName))
                }.onFailure {
                    onMessage(strings.saveErrorMessage)
                }
            }
        }
    }
    val projectIconPickerLauncher = remember(onMessage, strings) {
        object : ProjectIconPickerLauncher {
            override fun pickImage(onImagePicked: (ByteArray?) -> Unit) {
                val chooser = JFileChooser().apply {
                    dialogTitle = strings.chooseProjectIconDialogTitle
                    fileFilter = FileNameExtensionFilter(strings.imageFilesLabel, "png", "jpg", "jpeg", "webp")
                }
                val result = chooser.showOpenDialog(null)
                if (result != JFileChooser.APPROVE_OPTION) {
                    onImagePicked(null)
                    return
                }
                runCatching {
                    chooser.selectedFile.readBytes()
                }.onSuccess {
                    onImagePicked(it)
                }.onFailure {
                    onMessage(strings.unableToReadSelectedImage)
                    onImagePicked(null)
                }
            }
        }
    }
    val brandLogoPickerLauncher = remember(onMessage, strings) {
        object : BrandLogoPickerLauncher {
            override fun pickImage(onImagePicked: (ByteArray?) -> Unit) {
                val chooser = JFileChooser().apply {
                    dialogTitle = strings.chooseBrandLogoDialogTitle
                    fileFilter = FileNameExtensionFilter(strings.imageFilesLabel, "png", "jpg", "jpeg", "svg")
                }
                val result = chooser.showOpenDialog(null)
                if (result != JFileChooser.APPROVE_OPTION) {
                    onImagePicked(null)
                    return
                }
                runCatching {
                    chooser.selectedFile.readBytes()
                }.onSuccess { rawBytes ->
                    onImagePicked(normalizeBrandingLogoBytes(rawBytes) ?: byteArrayOf())
                }.onFailure {
                    onMessage(strings.unableToReadSelectedImage)
                    onImagePicked(null)
                }
            }
        }
    }

    return PlatformServices(
        storageDriver = storageDriver,
        fileSaveLauncher = fileSaveLauncher,
        projectIconPickerLauncher = projectIconPickerLauncher,
        brandLogoPickerLauncher = brandLogoPickerLauncher,
    )
}

private fun normalizeBrandingLogoBytes(rawBytes: ByteArray): ByteArray? {
    val sourceImage = if (isSvgImageBytes(rawBytes)) {
        renderSvgToBufferedImage(rawBytes) ?: return null
    } else {
        ImageIO.read(ByteArrayInputStream(rawBytes)) ?: return null
    }
    val targetSize = brandingLogoTargetSizeFor(sourceImage.width, sourceImage.height)
    val targetImage = BufferedImage(
        targetSize.width,
        targetSize.height,
        BufferedImage.TYPE_INT_RGB,
    )
    val graphics = targetImage.createGraphics()
    graphics.color = Color.WHITE
    graphics.fillRect(0, 0, targetSize.width, targetSize.height)
    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
    graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    val renderSize = computeBrandingLogoRenderSize(
        width = sourceImage.width,
        height = sourceImage.height,
        maxWidth = targetSize.width.toFloat(),
        maxHeight = targetSize.height.toFloat(),
    )
    val left = (targetSize.width - renderSize.width) / 2
    val top = (targetSize.height - renderSize.height) / 2
    graphics.drawImage(sourceImage, left, top, renderSize.width, renderSize.height, null)
    graphics.dispose()

    val normalizedBytes = ByteArrayOutputStream().use { output ->
        if (!ImageIO.write(targetImage, "jpg", output)) return@use null
        output.toByteArray()
    } ?: return null

    return normalizedBytes.takeIf { it.size <= MaxBrandingLogoBytes }
}

private fun renderSvgToBufferedImage(rawBytes: ByteArray): BufferedImage? {
    val sourceSize = parseSvgViewportSize(rawBytes)
        ?: BrandingLogoSize(
            width = BrandingRectangleLogoMaxWidthPx,
            height = BrandingRectangleLogoMaxHeightPx,
        )
    val targetSize = brandingLogoTargetSizeFor(sourceSize.width, sourceSize.height)
    val renderSize = computeBrandingLogoRenderSize(
        width = sourceSize.width,
        height = sourceSize.height,
        maxWidth = targetSize.width.toFloat(),
        maxHeight = targetSize.height.toFloat(),
    )
    val output = ByteArrayOutputStream()
    val transcoder = PNGTranscoder().apply {
        addTranscodingHint(PNGTranscoder.KEY_WIDTH, renderSize.width.toFloat())
        addTranscodingHint(PNGTranscoder.KEY_HEIGHT, renderSize.height.toFloat())
    }
    runCatching {
        ByteArrayInputStream(rawBytes).use { input ->
            transcoder.transcode(
                TranscoderInput(input),
                TranscoderOutput(output),
            )
        }
    }.getOrNull() ?: return null
    return ImageIO.read(ByteArrayInputStream(output.toByteArray()))
}

private class DesktopStorageDriver : StorageDriver {
    private val baseDirectory: Path = Path.of(System.getProperty("user.home"), ".tlincompose")

    override fun read(fileName: String): String? {
        val file = baseDirectory.resolve(fileName)
        return if (Files.exists(file)) Files.readString(file) else null
    }

    override fun write(fileName: String, content: String) {
        Files.createDirectories(baseDirectory)
        Files.writeString(baseDirectory.resolve(fileName), content)
    }
}

private fun com.tlincompose.domain.model.ExportDocument.extensionForChooser(): String = fileName.substringAfterLast('.', "")

private fun com.tlincompose.domain.model.ExportDocument.labelForChooser(strings: AppStrings): String =
    when (extensionForChooser().lowercase()) {
        "csv" -> strings.exportFormatLabel(com.tlincompose.domain.model.ExportFormat.CSV)
        "xlsx" -> strings.exportFormatLabel(com.tlincompose.domain.model.ExportFormat.XLSX)
        "pdf" -> strings.exportFormatLabel(com.tlincompose.domain.model.ExportFormat.PDF)
        else -> strings.appFileLabel
    }
