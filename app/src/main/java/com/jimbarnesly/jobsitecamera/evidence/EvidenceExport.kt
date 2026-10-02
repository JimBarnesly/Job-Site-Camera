package com.jimbarnesly.jobsitecamera.evidence

import android.graphics.BitmapFactory
import com.jimbarnesly.jobsitecamera.model.CaptureMetadata
import com.jimbarnesly.jobsitecamera.model.EvidenceStampConfig
import java.io.File
import java.io.FileOutputStream

class EvidenceExport(private val renderer: EvidenceStampRenderer = EvidenceStampRenderer()) {
    fun createStampedCopy(
        original: File,
        destination: File,
        metadata: CaptureMetadata,
        config: EvidenceStampConfig
    ): File {
        val source = requireNotNull(BitmapFactory.decodeFile(original.absolutePath)) { "Unable to decode source image" }
        val stamped = renderer.render(source, metadata, config)
        destination.parentFile?.mkdirs()
        FileOutputStream(destination).use { out ->
            stamped.compress(android.graphics.Bitmap.CompressFormat.JPEG, 95, out)
        }
        if (stamped !== source) stamped.recycle()
        source.recycle()
        return destination
    }
}
