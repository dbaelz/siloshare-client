package de.dbaelz.siloshare.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.dbaelz.siloshare.getPlatform
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = koinViewModel()

    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        SettingsTextField(
            value = state.host,
            onValueChange = {
                viewModel.sendEvent(
                    SettingsViewModelContract.Event.OnValuesChanged(
                        host = it,
                        port = state.port,
                        username = state.username,
                        password = state.password
                    )
                )
            },
            label = "Host Address"
        )

        SettingsTextField(
            value = state.port.toString(),
            onValueChange = { value ->
                value.toIntOrNull()?.takeIf { it > 0 }?.let { port ->
                    viewModel.sendEvent(
                        SettingsViewModelContract.Event.OnValuesChanged(
                            host = state.host,
                            port = port,
                            username = state.username,
                            password = state.password
                        )
                    )
                }
            },
            label = "Port",
            keyboardType = KeyboardType.Number
        )

        SettingsTextField(
            value = state.username,
            onValueChange = {
                viewModel.sendEvent(
                    SettingsViewModelContract.Event.OnValuesChanged(
                        host = state.host,
                        port = state.port,
                        username = it,
                        password = state.password
                    )
                )
            },
            label = "Basic Auth username"
        )


        SettingsTextField(
            value = state.password,
            onValueChange = {
                viewModel.sendEvent(
                    SettingsViewModelContract.Event.OnValuesChanged(
                        host = state.host,
                        port = state.port,
                        username = state.username,
                        password = it
                    )
                )
            },
            label = "Basic Auth password"
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Platform: ${getPlatform().name}",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SettingsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
