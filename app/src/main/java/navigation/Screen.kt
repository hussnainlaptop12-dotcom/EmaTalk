package com.example.ematalk.navigation

sealed class Screen(val route: String) {

    object Splash : Screen("splash")

    object Login : Screen("login")

    object Register : Screen("register")

    object Home : Screen("home")

    object Chat : Screen("chat")

    object Games : Screen("games")

    object Notification : Screen("notification")

    object Profile : Screen("profile")

    object EditProfile : Screen("edit_profile")

    object Search : Screen("search")

    object Friends : Screen("friends")

    object FriendRequests : Screen("friend_requests")

    object VoiceRooms : Screen("voice_rooms")

    object CreateVoiceRoom : Screen("create_voice_room")

    object Room : Screen("room/{roomId}") {

        fun createRoute(roomId: String): String {
            return "room/$roomId"
        }
    }
}