package com.tifd.projectcomposed.screen

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tifd.projectcomposed.AuthActivity
import com.tifd.projectcomposed.MainActivity
//import com.tifd.projectcomposed.Schedule

data class Schedule(
    val hari: String = "",
    val jam: String = "",
    val matkul: String = "",
    val praktikum: Boolean = false,
    val ruang: String = ""
)

@Composable
fun MatkulScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    var schedules by remember { mutableStateOf<List<Schedule>>(emptyList()) }

    val hariOrder = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")

    LaunchedEffect(Unit) {
        db.collection("matakuliah")
            .get()
            .addOnSuccessListener { result ->
                schedules = result.documents.mapNotNull { doc ->
                    doc.toObject(Schedule::class.java)
                }.sortedWith(compareBy { hariOrder.indexOf(it.hari) })
            }
    }

    Scaffold(

        content = { paddingValues ->
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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


            }
        }
    )
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