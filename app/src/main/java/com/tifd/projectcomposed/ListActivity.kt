package com.tifd.projectcomposed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme

class ListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectComposeDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

data class Schedule(
    val hari: String = "",
    val jam: String = "",
    val matkul: String = "",
    val praktikum: Boolean = false,
    val ruang: String = ""
)

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    var schedules by remember { mutableStateOf<List<Schedule>>(emptyList()) }

    // Define the custom order for "Hari"
    val hariOrder = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")

    LaunchedEffect(Unit) {
        db.collection("matakuliah")  // replace with your Firestore collection name
            .get()
            .addOnSuccessListener { result ->
                schedules = result.documents.mapNotNull { doc ->
                    doc.toObject(Schedule::class.java)
                }.sortedWith(compareBy { hariOrder.indexOf(it.hari) })
            }

    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Jadwal Kuliah Gw")

        // Scrollable list of schedule cards
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Take up remaining vertical space
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Space between items
        ) {
            items(schedules) { schedule ->
                ScheduleCard(schedule)
            }
        }

        Button(
            onClick = {
                auth.signOut()
                context.startActivity(Intent(context, MainActivity::class.java))
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6200EE), // Deep purple when enabled
                contentColor = Color.White, // White text color
            )
        ) {
            Text(text = "Logout")
        }
    }
}

@Composable
fun ScheduleCard(schedule: Schedule) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFBB86FC)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Hari: ${schedule.hari}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Jam: ${schedule.jam}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Matkul: ${schedule.matkul}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Praktikum: ${if (schedule.praktikum) "Praktikum" else "Non Praktikum"}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Ruang: ${schedule.ruang}")
        }
    }
}
