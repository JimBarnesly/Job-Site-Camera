package com.jimbarnesly.jobsitecamera.model

data class EvidenceStampConfig(
    val enabled: Boolean = true,
    val showProject: Boolean = true,
    val showClient: Boolean = true,
    val showDateTime: Boolean = true,
    val showCoordinates: Boolean = true,
    val showCategory: Boolean = true,
    val showAsset: Boolean = true,
    val showNote: Boolean = true
)

data class CaptureMetadata(
    val projectName: String,
    val client: String = "",
    val capturedAt: Long = java.lang.System.currentTimeMillis(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val category: WorkCategory = WorkCategory.OTHER,
    val asset: String = "",
    val note: String = ""
)
