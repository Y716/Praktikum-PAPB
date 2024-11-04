package com.tifd.projectcomposed.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tifd.projectcomposed.data.model.local.TugasRepository
import com.tifd.projectcomposed.data.model.local.Tugas
import com.tifd.projectcomposed.viewmodel.TugasViewModel
import com.tifd.projectcomposed.viewmodel.TugasViewModelFactory
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import coil.compose.rememberImagePainter
import com.tifd.projectcomposed.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TugasScreen(
    tugasRepository: TugasRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var showCameraPreview by remember { mutableStateOf(false) }


    // Permission request launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            if (granted) {
                showCameraPreview = true // Show the camera preview if permission is granted
            }
        }


    )
    var showCamera by remember { mutableStateOf(false) }


    // Check if permission is granted
    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val tugasViewModel: TugasViewModel = viewModel(factory = TugasViewModelFactory(tugasRepository))
    var matkul by remember { mutableStateOf(TextFieldValue("")) }
    var detailTugas by remember { mutableStateOf(TextFieldValue("")) }
    var deadline by remember { mutableStateOf("") } // For displaying selected date
    var imageUri by remember { mutableStateOf<Uri?>(null) } // Temporary variable to store image URI



    val listTugas by tugasViewModel.listTugas.observeAsState(emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Initialize date picker variables
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // State for image viewer dialog
    var isImageDialogVisible by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }


    // Date Picker Dialog setup
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            deadline = dateFormat.format(calendar.time) // Update deadline with chosen date
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    if (showCamera && hasCameraPermission) {
        FullScreenCamera(
            onImageCaptured = { uri ->
                // Store URI temporarily for display
                imageUri = uri
                showCamera = false
            },
            onClose = { showCamera = false }
        )
    }else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.height(30.dp))

                // Fields for Task Name and Details
                TextField(
                    value = matkul,
                    onValueChange = { matkul = it },
                    label = { Text("Nama Matkul") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = detailTugas,
                    onValueChange = { detailTugas = it },
                    label = { Text("Detail Tugas") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showCamera = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EE),
                        contentColor = Color.White,
                    )) {
                    Text("Activate Camera")
                }

                Spacer(modifier = Modifier.height(16.dp))

                imageUri?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "File name: ${it.lastPathSegment}", fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Picker TextField
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { },
                    label = { Text("Deadline") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },  // Show date picker on click
                    placeholder = { Text("Pick a date") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Optional Button to test Date Picker
                Button(
                    onClick = { datePickerDialog.show() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EE),
                        contentColor = Color.White,
                    )
                ) {
                    Text("Select Deadline Date")
                }


                Button(
                    onClick = {
                        if (matkul.text.isNotEmpty() && detailTugas.text.isNotEmpty() && deadline.isNotEmpty()) {
                            imageUri?.let {
                                tugasViewModel.addTugas(matkul.text, detailTugas.text, deadline, it)
                            }

                            scope.launch {
                                snackbarHostState.showSnackbar("Tugas berhasil ditambahkan")
                            }
                            matkul = TextFieldValue("")
                            detailTugas = TextFieldValue("")
                            deadline = ""
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("All fields must be filled")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EE),
                        contentColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Tambah Tugas")
                }

                SnackbarHost(hostState = snackbarHostState)

                Spacer(modifier = Modifier.height(16.dp))

                // Display List of Tasks
                if (listTugas.isNotEmpty()) {
                    LazyColumn {
                        items(listTugas) { tugas ->
                            TugasItem(
                                tugas = tugas,
                                onDoneClicked = { done ->
                                    tugasViewModel.updateTugasCompletion(tugas.id, done)
                                },
                                onDeleteClick = {
                                    tugasViewModel.deleteTugas(tugas.id)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Tugas berhasil dihapus")
                                    }
                                },
                                onViewImageClick = {
                                    selectedImageUri = tugas.imageUri
                                    isImageDialogVisible = true
                                }
                            )
                        }
                    }
                } else {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Tidak ada tugas yang ditambahkan",
                        fontSize = 16.sp
                    )
                }

                if (isImageDialogVisible) {
                    ImageViewerDialog(
                        imageUri = selectedImageUri?.let { Uri.parse(it) }, // Convert String back to Uri
                        onDismiss = { isImageDialogVisible = false } // Close the dialog
                    )
                }
            }
        }
    }

}

@Composable
fun ImageViewerDialog(
    imageUri: Uri?,
    onDismiss: () -> Unit
) {
    if (imageUri != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "View Image") },
            text = {
                Image(
                    painter = rememberImagePainter(imageUri),
                    contentDescription = "Captured Image",
                    modifier = Modifier.fillMaxWidth().height(300.dp) // Set desired size
                )
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        )
    }
}


@Composable
fun FullScreenCamera(
    onImageCaptured: (Uri) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var previewView by remember {
        mutableStateOf(
            PreviewView(context).apply {
                this.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                this.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        )
    }

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    // Handle camera setup when the view is first composed
    LaunchedEffect(previewView) {
        imageCapture = ImageCapture.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()

        try {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Unbind all use cases before rebinding
            cameraProvider.unbindAll()
            // Bind use cases to camera
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Camera Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                onClick = {
                    // Create output file
                    val photoFile = File(
                        context.cacheDir,
                        "captured_image_${System.currentTimeMillis()}.jpg"
                    )
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    // Take picture
                    imageCapture?.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val savedUri = Uri.fromFile(photoFile)
                                onImageCaptured(savedUri)
                                onClose()
                            }

                            override fun onError(exception: ImageCaptureException) {
                                exception.printStackTrace()
                            }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text("Take Photo")
            }

            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}



@Composable
fun TugasItem(
    tugas: Tugas,
    onDoneClicked: (Boolean) -> Unit,
    isCompleted: Boolean = false,
    onDeleteClick: (() -> Unit)? = null,
    onViewImageClick: (() -> Unit)? = null
) {
    var isDone by remember { mutableStateOf(tugas.selesai) }
    val checkboxColor = if (isDone) MaterialTheme.colorScheme.primary else Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) Color(0xFFEEEEEE) else MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDone) 0.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(2.dp, checkboxColor, CircleShape)
                    .background(if (isDone) checkboxColor else Color.Transparent)
                    .clickable {
                        isDone = !isDone
                        onDoneClicked(isDone)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tugas.matkul,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                )
                if (tugas.detail_tugas.isNotEmpty()) {
                    Text(
                        text = tugas.detail_tugas,
                        style = MaterialTheme.typography.bodyMedium,
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                if (tugas.deadline.isNotEmpty()) {
                    Text(
                        text = "Deadline: ${tugas.deadline}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Delete Button
            if (onDeleteClick != null) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus tugas",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }

            // View Image Button
            if (onViewImageClick != null) {
                IconButton(
                    onClick = onViewImageClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_visibility_24),
                        contentDescription = "View Image",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

