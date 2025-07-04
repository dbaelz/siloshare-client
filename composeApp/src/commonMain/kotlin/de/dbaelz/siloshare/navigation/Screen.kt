package de.dbaelz.siloshare.navigation

enum class Screen(val title: String, val actions: Set<Action> = emptySet()) {
    Notes(
        title = "Notes",
        actions = setOf(Action.ShowSettings)
    ),
    Settings(
        title = "Settings",
        actions = setOf(Action.SaveSettings)
    );
}

