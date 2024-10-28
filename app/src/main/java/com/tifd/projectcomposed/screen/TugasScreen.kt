package com.tifd.projectcomposed.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TugasScreen(
    tugasRepository: TugasRepository,
    onNavigateBack: () -> Unit
) {
    val tugasViewModel: TugasViewModel = viewModel(factory = TugasViewModelFactory(tugasRepository))
    var matkul by remember { mutableStateOf(TextFieldValue("")) }
    var detailTugas by remember { mutableStateOf(TextFieldValue("")) }
    var deadline by remember { mutableStateOf("") } // For displaying selected date

    val listTugas by tugasViewModel.listTugas.observeAsState(emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Initialize date picker variables
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

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
                )) {
                Text("Select Deadline Date")
            }


            Button(
                onClick = {
                    if (matkul.text.isNotEmpty() && detailTugas.text.isNotEmpty() && deadline.isNotEmpty()) {
                        tugasViewModel.addTugas(matkul.text, detailTugas.text, deadline)
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
                            }
                        )
                    }
                }
            }else {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Tidak ada tugas yang ditambahkan",
                    fontSize = 16.sp
                )
            }
        }
    }

}

@Composable
fun TugasItem(
    tugas: Tugas,
    onDoneClicked: (Boolean) -> Unit,
    isCompleted: Boolean = false,
    onDeleteClick: (() -> Unit)? = null
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
        }
    }
}