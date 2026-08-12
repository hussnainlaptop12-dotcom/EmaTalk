package com.example.ematalk.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ematalk.auth.LoginScreen
import com.example.ematalk.auth.RegisterScreen
import com.example.ematalk.chat.ChatScreen
import com.example.ematalk.friend.FriendRequestScreen
import com.example.ematalk.friend.FriendsScreen
import com.example.ematalk.games.GamesScreen
import com.example.ematalk.home.HomeScreen
import com.example.ematalk.notification.NotificationScreen
import com.example.ematalk.profile.EditProfileScreen
import com.example.ematalk.profile.ProfileScreen
import com.example.ematalk.room.RoomScreen
import com.example.ematalk.splash.SplashScreen
import com.example.ematalk.user.SearchScreen
import com.example.ematalk.voice.CreateVoiceRoomScreen
import com.example.ematalk.voice.VoiceRoomScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.Chat.route) {
            ChatScreen(navController)
        }

        composable(Screen.Games.route) {
            GamesScreen()
        }

        composable(Screen.Notification.route) {
            NotificationScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen()
        }

        composable(Screen.Search.route) {
            SearchScreen()
        }

        composable(Screen.Friends.route) {
            FriendsScreen()
        }

        composable(Screen.FriendRequests.route) {
            FriendRequestScreen()
        }

        composable(Screen.VoiceRooms.route) {
            VoiceRoomScreen(navController)
        }

        composable(Screen.CreateVoiceRoom.route) {
            CreateVoiceRoomScreen(navController)
        }
        composable(
            route = Screen.Room.route,
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val roomId =
                backStackEntry.arguments?.getString("roomId").orEmpty()

            RoomScreen(
                roomId = roomId,
                navController = navController
            )
        }
    }
}