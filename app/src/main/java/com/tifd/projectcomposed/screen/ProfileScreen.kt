package com.tifd.projectcomposed.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


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
fun ProfileScreen() {
    var user by remember { mutableStateOf<GitHubUser?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

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
