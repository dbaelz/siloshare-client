package de.dbaelz.siloshare

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform