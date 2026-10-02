package com.jimbarnesly.jobsitecamera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.jimbarnesly.jobsitecamera.storage.WorkPhotoStorage

class MainActivity : ComponentActivity() {
    private val requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) requestCamera.launch(Manifest.permission.CAMERA)
        setContent { MaterialTheme { Surface(Modifier.fillMaxSize()) { JobSiteCameraApp() } } }
    }
}

@Composable
private fun JobSiteCameraApp() {
    var project by remember { mutableStateOf<String?>(null) }
    if (project == null) ProjectPicker(onOpen = { project = it }) else CameraScreen(project = project!!, onBack = { project = null })
}

@Composable
private fun ProjectPicker(onOpen: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Job Site Camera", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Work photos stay out of your personal gallery.")
        Spacer(Modifier.height(24.dp))
        Button(onClick = { onOpen("current-project") }) { Text("Open current project") }
    }
}

@Composable
private fun CameraScreen(project: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val storage = remember { WorkPhotoStorage(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var lastSaved by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Current Project") }, navigationIcon = { TextButton(onClick = onBack) { Text("Projects") } })

        AndroidView(modifier = Modifier.weight(1f).fillMaxWidth(), factory = { ctx ->
            val previewView = PreviewView(ctx)
            val providerFuture = ProcessCameraProvider.getInstance(ctx)
            providerFuture.addListener({
                val provider = providerFuture.get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                val capture = ImageCapture.Builder().build()
                imageCapture = capture
                provider.unbindAll()
                provider.bindToLifecycle(context as ComponentActivity, CameraSelector.DEFAULT_BACK_CAMERA, preview, capture)
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        })

        lastSaved?.let { Text("Saved privately: ${it.substringAfterLast('/')}", modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) }

        Row(Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = {}) { Text("Gallery") }
            Button(onClick = {
                val capture = imageCapture ?: return@Button
                val file = storage.newPhotoFile(project)
                val output = ImageCapture.OutputFileOptions.Builder(file).build()
                capture.takePicture(output, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(result: ImageCapture.OutputFileResults) { lastSaved = file.absolutePath }
                    override fun onError(exception: ImageCaptureException) = Unit
                })
            }) { Text("Capture") }
            OutlinedButton(onClick = {}) { Text("Note") }
        }
    }
}
