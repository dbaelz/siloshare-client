package de.dbaelz.siloshare

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.dbaelz.siloshare.feature.notes.NotesScreen
import de.dbaelz.siloshare.feature.settings.SettingsScreen
import de.dbaelz.siloshare.navigation.Screen
import de.dbaelz.siloshare.ui.theme.AppTheme
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.getKoin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    Napier.base(DebugAntilog())
    val navController: NavHostController = rememberNavController()

    KoinApplication(
        application = { modules(appModule(navController)) }
    ) {
        AppTheme {
            val actionDispatcher: ActionDispatcher = getKoin().get()

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
                            if (currentScreen != Screen.Notes) {
                                IconButton(onClick = {
                                    navController.navigateUp()
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        },
                        actions = {
                            currentScreen.actions.forEach { action ->
                                IconButton(onClick = {
                                    actionDispatcher.dispatch(action)
                                }) {
                                    Icon(
                                        imageVector = action.icon,
                                        contentDescription = action.description,
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
                        NotesScreen()
                    }

                    composable(route = Screen.AddNote.name) {
                        // TODO: Implement AddNoteScreen
                    }

                    composable(route = Screen.Settings.name) {
                        SettingsScreen()
                    }
                }
            }
        }
    }
}