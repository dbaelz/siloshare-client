package de.dbaelz.siloshare.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = koinViewModel()

    val state = viewModel.state.collectAsState().value

    var host by remember { mutableStateOf(state.host) }
    var username by remember { mutableStateOf(state.username) }
    var password by remember { mutableStateOf(state.password) }

    Column(modifier = Modifier.padding(16.dp)) {
        SettingsTextField(
            value = host,
            onValueChange = { host = it },
            label = "Host Address"
        )

        SettingsTextField(
            value = username,
            onValueChange = { username = it },
            label = "Basic Auth username"
        )


        SettingsTextField(
            value = password,
            onValueChange = { password = it },
            label = "Basic Auth password"
        )
    }
}

@Composable
fun SettingsTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    )
}
