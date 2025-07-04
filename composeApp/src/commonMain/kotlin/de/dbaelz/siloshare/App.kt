package de.dbaelz.siloshare

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.dbaelz.siloshare.feature.settings.SettingsScreen
import de.dbaelz.siloshare.feature.settings.SettingsViewModelContract
import de.dbaelz.siloshare.navigation.Screen
import de.dbaelz.siloshare.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    AppTheme {
        val navController: NavHostController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentScreen = Screen.valueOf(
            backStackEntry?.destination?.route ?: Screen.Notes.name
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(currentScreen.title) },
                    navigationIcon = {
                        // TODO: Implement navigation icon if needed

                    },
                    actions = {
                        if (currentScreen == Screen.Notes) {
                            IconButton(onClick = {
                                navController.navigate(Screen.Settings.name)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Notes.name,
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                composable(route = Screen.Notes.name) {
                    // TODO: Implement NotesScreen
                }

                composable(route = Screen.Settings.name) {
                    // TODO: Implement correct SettingsScreen and use the ViewModel
                    SettingsScreen(
                        state = SettingsViewModelContract.State(
                            host = "http://localhost:8080",
                            username = "user",
                            password = "password"
                        ),
                        onEvent = {}
                    )
                }
            }
        }
    }
}