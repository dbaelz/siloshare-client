package de.dbaelz.siloshare.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Action(
    val icon: ImageVector,
    val description: String
) {
    data object ShowSettings : Action(Icons.Default.Settings, "Show settings screen")

    data object SaveSettings : Action(Icons.Default.Save, "Save settings")
}