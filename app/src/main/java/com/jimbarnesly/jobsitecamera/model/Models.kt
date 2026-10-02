package com.jimbarnesly.jobsitecamera.model

data class Project(val id: String, val name: String, val client: String = "")

enum class WorkCategory(val label: String) {
    SWITCHBOARD("Switchboard"),
    CABLE_DRUM("Cable drum"),
    CABLE_ROUTE("Cable route / trench"),
    DOCUMENT("Drawing / document"),
    NAMEPLATE("Nameplate"),
    TEST_READING("Test equipment / reading"),
    DEFECT("Defect / snag"),
    PROGRESS("Progress"),
    SITE_OVERVIEW("Site overview"),
    RECEIPT("Receipt / docket"),
    TOOLS_MATERIALS("Tools / materials"),
    OTHER("Other")
}

data class WorkPhoto(
    val path: String,
    val projectId: String,
    val capturedAt: Long,
    val category: WorkCategory = WorkCategory.OTHER,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val asset: String = "",
    val note: String = "",
    val evidenceMode: Boolean = false
)
