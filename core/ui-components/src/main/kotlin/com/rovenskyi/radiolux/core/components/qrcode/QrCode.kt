package com.rovenskyi.radiolux.core.components.qrcode

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * Displays a QR code for the given content.
 *
 * Uses ZXing library to generate QR code matrix and renders it using Compose Canvas.
 * Designed for TV use where URLs cannot be opened directly.
 *
 * @param content The text/URL to encode as QR code
 * @param size The size of the QR code (width = height)
 * @param modifier Modifier to apply
 * @param foregroundColor Color for QR code modules (default: onSurface)
 * @param backgroundColor Color for QR code background (default: surface)
 * @param contentDescription Accessibility description for the QR code
 */
@Composable
fun QrCode(
    content: String,
    size: Dp,
    modifier: Modifier = Modifier,
    foregroundColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    @Suppress("UNUSED_PARAMETER") contentDescription: String? = null,
) {
    val qrMatrix = remember(content) {
        generateQrMatrix(content)
    }

    Canvas(
        modifier = modifier.size(size),
    ) {
        if (qrMatrix != null) {
            drawQrCode(qrMatrix, foregroundColor, backgroundColor)
        }
    }
}

/**
 * Generates QR code bit matrix from content string.
 *
 * @param content Text to encode
 * @return 2D boolean array where true = dark module, false = light module
 */
private fun generateQrMatrix(content: String): Array<BooleanArray>? {
    return try {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.MARGIN to 1,
        )

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 0, 0, hints)

        val width = bitMatrix.width
        val height = bitMatrix.height

        Array(height) { y ->
            BooleanArray(width) { x ->
                bitMatrix.get(x, y)
            }
        }
    } catch (_: Exception) {
        null
    }
}

/**
 * Draws QR code matrix on Canvas.
 */
private fun DrawScope.drawQrCode(
    matrix: Array<BooleanArray>,
    foregroundColor: Color,
    backgroundColor: Color,
) {
    val matrixHeight = matrix.size
    val matrixWidth = if (matrixHeight > 0) matrix[0].size else 0

    if (matrixWidth == 0 || matrixHeight == 0) return

    val cellWidth = size.width / matrixWidth
    val cellHeight = size.height / matrixHeight

    // Draw background
    drawRect(color = backgroundColor, size = size)

    // Draw QR modules
    for (y in 0 until matrixHeight) {
        for (x in 0 until matrixWidth) {
            if (matrix[y][x]) {
                drawRect(
                    color = foregroundColor,
                    topLeft = Offset(x * cellWidth, y * cellHeight),
                    size = Size(cellWidth, cellHeight),
                )
            }
        }
    }
}
