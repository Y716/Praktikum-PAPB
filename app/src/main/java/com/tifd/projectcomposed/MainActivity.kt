package com.tifd.projectcomposed

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tifd.projectcomposed.data.model.local.TugasRepository

import com.tifd.projectcomposed.navigation.NavigationItem
import com.tifd.projectcomposed.navigation.Screen
import com.tifd.projectcomposed.screen.MatkulScreen
import com.tifd.projectcomposed.screen.ProfileScreen
import com.tifd.projectcomposed.screen.TugasScreen
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            ProjectComposeDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainActivityContent(
                        tugasRepository = (application as MyApplication).tugasRepository
                    )
                }
            }
        }
    }
}

@Composable
fun MainActivityContent(
    modifier: Modifier = Modifier,
    tugasRepository: TugasRepository,
    navController: NavHostController = rememberNavController(),
) {
    Scaffold(
        bottomBar = { BottomAppBar(navController)},
        modifier = modifier
    ) {
        innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Matkul.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
            ){
            composable(Screen.Matkul.route){
                MatkulScreen()
            }
            composable(Screen.Tugas.route){
                TugasScreen(
                    tugasRepository = tugasRepository,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable(Screen.Profil.route){
                ProfileScreen()
            }
        }
    }
}


@Composable
private fun BottomAppBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry?.destination?.route


        val navigationItems = listOf(
            NavigationItem(
                title = stringResource(R.string.matkul),
                icon = Icons.Default.Search,
                screen = Screen.Matkul
            ),
            NavigationItem(
                title = stringResource(R.string.tugas),
                icon = Icons.Default.Favorite,
                screen = Screen.Tugas
            ),
            NavigationItem(
                title = stringResource(R.string.profil),
                icon = Icons.Default.Search,
                screen = Screen.Profil
            ),
        )
        navigationItems.map { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )

        }

    }
}

class MyApplication : Application() {
    lateinit var tugasRepository: TugasRepository

    override fun onCreate() {
        super.onCreate()
        tugasRepository = TugasRepository(this)
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProjectComposeDTheme {
//        MyScreen()
    }
}
