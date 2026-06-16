package com.tlincompose.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.core.brandingLogoPlaceholder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
internal fun BrandingLogoPreview(
    brandingLogoBase64: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    showPlaceholderWhenEmpty: Boolean = false,
) {
    val strings = LocalAppStrings.current
    val decodedLogo = remember(brandingLogoBase64) {
        decodeBase64Image(brandingLogoBase64)
    }
    if (decodedLogo == null && !showPlaceholderWhenEmpty) return

    Surface(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (decodedLogo != null) {
                Image(
                    bitmap = decodedLogo,
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                        .padding(10.dp),
                    contentScale = ContentScale.Fit,
                )
            } else {
                Text(
                    text = strings.brandingLogoPlaceholder,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
internal fun decodeBase64Image(base64: String?): ImageBitmap? {
    if (base64.isNullOrBlank()) return null
    return runCatching {
        Base64.Default.decode(base64).decodeToImageBitmap()
    }.getOrNull()
}
