package com.example.ematalk.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(

    val route: String,
    val title: String,
    val icon: ImageVector

) {

    object Home : BottomNavItem(
        "home",
        "Home",
        Icons.Default.Home
    )

    object Chat : BottomNavItem(
        "chat",
        "Chat",
        Icons.Default.Chat
    )

    object Games : BottomNavItem(
        "games",
        "Games",
        Icons.Default.SportsEsports
    )

    object Notification : BottomNavItem(
        "notification",
        "Alerts",
        Icons.Default.Notifications
    )

    object Profile : BottomNavItem(
        "profile",
        "Profile",
        Icons.Default.Person
    )

}

