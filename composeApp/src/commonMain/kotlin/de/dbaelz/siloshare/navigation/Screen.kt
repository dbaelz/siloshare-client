package de.dbaelz.siloshare.navigation

enum class Screen(val title: String, val actions: Set<Action> = emptySet()) {
    Notes(
        title = "Notes",
        actions = setOf(Action.NotesRefresh, Action.NotesAdd, Action.NotesShowSettings)
    ),

    AddNote(
        title = "Add Note",
        actions = setOf(Action.AddNoteSave)
    ),
    Settings(
        title = "Settings",
        actions = setOf(Action.SettingsSave)
    );
}

