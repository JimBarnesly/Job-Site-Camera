package com.jimbarnesly.jobsitecamera.evidence

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.jimbarnesly.jobsitecamera.model.CaptureMetadata
import com.jimbarnesly.jobsitecamera.model.EvidenceStampConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EvidenceStampRenderer {
    fun render(source: Bitmap, metadata: CaptureMetadata, config: EvidenceStampConfig): Bitmap {
        if (!config.enabled) return source.copy(source.config ?: Bitmap.Config.ARGB_8888, false)

        val output = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(output)
        val scale = (source.width / 1080f).coerceAtLeast(0.6f)
        val padding = 28f * scale
        val lineGap = 12f * scale
        val textSize = 34f * scale

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            this.textSize = textSize
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.BOLD)
        }

        val lines = buildLines(metadata, config)
        if (lines.isEmpty()) return output

        val fm = textPaint.fontMetrics
        val lineHeight = (fm.descent - fm.ascent) + lineGap
        val boxHeight = padding * 2 + lineHeight * lines.size

        val boxPaint = Paint().apply { color = Color.argb(170, 0, 0, 0) }
        canvas.drawRect(0f, source.height - boxHeight, source.width.toFloat(), source.height.toFloat(), boxPaint)

        var y = source.height - boxHeight + padding - fm.ascent
        for (line in lines) {
            val safe = ellipsize(line, textPaint, source.width - padding * 2)
            canvas.drawText(safe, padding, y, textPaint)
            y += lineHeight
        }
        return output
    }

    private fun buildLines(metadata: CaptureMetadata, config: EvidenceStampConfig): List<String> {
        val lines = mutableListOf<String>()
        if (config.showProject && metadata.projectName.isNotBlank()) lines += metadata.projectName
        if (config.showClient && metadata.client.isNotBlank()) lines += "Client: ${metadata.client}"
        if (config.showDateTime) {
            val fmt = SimpleDateFormat("dd MMM yyyy  HH:mm:ss", Locale.getDefault())
            lines += fmt.format(Date(metadata.capturedAt))
        }
        if (config.showCoordinates && metadata.latitude != null && metadata.longitude != null) {
            lines += String.format(Locale.US, "%.6f, %.6f", metadata.latitude, metadata.longitude)
        }
        if (config.showCategory) lines += "Category: ${metadata.category.label}"
        if (config.showAsset && metadata.asset.isNotBlank()) lines += "Asset: ${metadata.asset}"
        if (config.showNote && metadata.note.isNotBlank()) lines += metadata.note
        return lines
    }

    private fun ellipsize(text: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        var end = text.length
        while (end > 1 && paint.measureText(text.substring(0, end) + "…") > maxWidth) end--
        return text.substring(0, end) + "…"
    }
}
