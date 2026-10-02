package com.jimbarnesly.jobsitecamera.storage

import android.content.Context
import java.io.File

class WorkPhotoStorage(private val context: Context) {
    fun newPhotoFile(projectId: String): File {
        val dir = File(context.filesDir, "projects/$projectId/photos").apply { mkdirs() }
        return File(dir, "IMG_${java.lang.System.currentTimeMillis()}.jpg")
    }

    fun photosFor(projectId: String): List<File> {
        val dir = File(context.filesDir, "projects/$projectId/photos")
        return dir.listFiles()?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
}
