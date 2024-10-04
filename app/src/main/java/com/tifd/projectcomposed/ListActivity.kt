package com.tifd.projectcomposed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class ListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectComposeDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Setup Navigation
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { HomeScreen(navController) }
                        composable("github_profile") { GithubProfile(navController) }
                    }
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

// Retrofit GitHub API Setup
interface GitHubService {
    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): GitHubUser
}

data class GitHubUser(
    val login: String,
    val name: String?,
    val avatar_url: String,
    val followers: Int,
    val following: Int
)

val retrofit = Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val gitHubService = retrofit.create(GitHubService::class.java)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
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
        topBar = {
            TopAppBar(
                title = { Text("Jadwal Kuliah Gw") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("github_profile")
                    }) {
//                        Icon(
//                            imageVector = Icons.Default.AccountCircle, // Replace with GitHub icon
//                            contentDescription = "GitHub Profile",
//                            tint = Color.White
//                        )
                        // Alternatively, use painterResource for custom icon
                        Icon(
                            painter = painterResource(id = R.drawable.github_icon),
                            contentDescription = "GitHub Profile",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(48.dp)
                        )

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = {
//                    navController.navigate("github_profile")
//                }
////                backgroundColor = Color(0xFF6200EE)
//            ) {
////                Icon(
////                    imageVector = Icons.Default.AccountCircle, // Replace with GitHub icon
////                    contentDescription = "GitHub Profile",
////                    tint = Color.White
////                )
//                // Alternatively, use painterResource for custom icon
//                Icon(
//                    painter = painterResource(id = R.drawable.github_icon),
//                    contentDescription = "GitHub Profile",
//                    tint = Color.Unspecified
//                )
//
//            }
//        },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GithubProfile(navController: NavHostController) {
    var user by remember { mutableStateOf<GitHubUser?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Replace with your GitHub username
    val githubUsername = "Y716"

    LaunchedEffect(Unit) {
        try {
            user = gitHubService.getUser(githubUsername)
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GitHub Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator()
                    }
                    error != null -> {
                        Text(text = "Error: $error")
                    }
                    user != null -> {
                        GitHubProfileContent(user!!)
                    }
                }
            }
        }
    )
}

@Composable
fun GitHubProfileContent(user: GitHubUser) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        // Profile Image
        Image(
            painter = rememberAsyncImagePainter(model = user.avatar_url),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(128.dp)
                .padding(8.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Username
        Text(text = user.login, fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Name
        user.name?.let {
            Text(text = it, fontSize = 20.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Followers and Following
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${user.followers}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Followers", fontSize = 16.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${user.following}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Following", fontSize = 16.sp)
            }
        }
    }
}
