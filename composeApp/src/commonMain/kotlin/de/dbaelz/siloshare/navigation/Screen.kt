package de.dbaelz.siloshare.navigation

enum class Screen(val title: String, val actions: Set<Action> = emptySet()) {
    Notes(
        title = "Notes",
        actions = setOf(Action.NotesRefresh, Action.NotesShowSettings)
    ),
    Settings(
        title = "Settings",
        actions = setOf(Action.SettingsSave)
    );
}

