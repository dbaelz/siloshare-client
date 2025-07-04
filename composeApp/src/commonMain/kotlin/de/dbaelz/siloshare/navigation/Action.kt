package de.dbaelz.siloshare.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Action(
    val icon: ImageVector,
    val description: String
) {
    data object NotesRefresh : Action(Icons.Default.Refresh, "Refresh notes")
    data object NotesShowSettings : Action(Icons.Default.Settings, "Show settings screen")
    data object SettingsSave : Action(Icons.Default.Save, "Save settings")
}