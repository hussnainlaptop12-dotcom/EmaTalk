package com.example.ematalk.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ematalk.navigation.Screen
import com.example.ematalk.voice.VoiceRoomModel
import com.google.firebase.firestore.FirebaseFirestore
@Composable
fun HomeScreen(
    navController: NavController
) {

    val db = FirebaseFirestore.getInstance()

    val rooms = remember {
        mutableStateListOf<VoiceRoomModel>()
    }

    // --------------------------------
    // FIREBASE LIVE ROOMS
    // --------------------------------

    DisposableEffect(Unit) {

        val listener = db
            .collection("voice_rooms")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {

                    val newRooms =
                        snapshot.documents.mapNotNull { document ->

                            try {

                                VoiceRoomModel(

                                    roomId =
                                        document.getString("roomId")
                                            ?: document.id,

                                    roomName =
                                        document.getString("roomName")
                                            ?: "Voice Room",

                                    hostId =
                                        document.getString("hostId")
                                            ?: "",

                                    hostName =
                                        document.getString("hostName")
                                            ?: "Host",

                                    users =
                                        (document.getLong("users")
                                            ?: 0L).toInt()
                                )

                            } catch (e: Exception) {

                                null
                            }
                        }

                    rooms.clear()
                    rooms.addAll(newRooms)
                }
            }

        onDispose {
            listener.remove()
        }
    }


    // --------------------------------
    // MAIN UI
    // --------------------------------

    Scaffold(

        containerColor = Color(0xFF07030D),

        bottomBar = {

            HomeBottomBar(
                navController = navController
            )
        },

        floatingActionButton = {

            FloatingActionButton(

                onClick = {

                    navController.navigate(
                        Screen.CreateVoiceRoom.route
                    )
                },

                modifier = Modifier.shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(22.dp)
                ),

                shape = RoundedCornerShape(20.dp),

                containerColor = Color(0xFF9A3DFF),

                contentColor = Color.White

            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Room"
                )
            }
        }

    ) { padding ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF07030D),
                            Color(0xFF12051F),
                            Color(0xFF07030D)
                        )
                    )
                ),

            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 90.dp
            ),

            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {


            // --------------------------------
            // HEADER
            // --------------------------------

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "EmaTalk",
                            color = Color.White,
                            fontSize = 27.sp,
                            fontWeight =
                                FontWeight.ExtraBold
                        )

                        Text(
                            text =
                                "Talk. Connect. Enjoy.",
                            color =
                                Color(0xFFAA91B9),
                            fontSize = 11.sp
                        )
                    }


                    HomeHeaderButton(
                        icon = Icons.Default.Search
                    ) {

                        navController.navigate(
                            Screen.Search.route
                        )
                    }


                    Spacer(
                        modifier = Modifier.width(7.dp)
                    )


                    HomeHeaderButton(
                        icon =
                            Icons.Default.Notifications
                    ) {

                        navController.navigate(
                            Screen.Notification.route
                        )
                    }


                    Spacer(
                        modifier = Modifier.width(7.dp)
                    )


                    Surface(
                        shape =
                            RoundedCornerShape(15.dp),
                        color =
                            Color(0xFF25172E)
                    ) {

                        Row(
                            modifier =
                                Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 8.dp
                                ),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                text = "🪙",
                                fontSize = 17.sp
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(4.dp)
                            )

                            Text(
                                text = "500",
                                color =
                                    Color(0xFFFFD15C),
                                fontWeight =
                                    FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }


            // --------------------------------
            // EVENT BANNER
            // --------------------------------

            item {

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .shadow(
                            elevation = 15.dp,
                            shape =
                                RoundedCornerShape(26.dp)
                        )
                        .background(
                            brush =
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF7B24B9),
                                        Color(0xFF3A176A),
                                        Color(0xFF16102D)
                                    )
                                ),
                            shape =
                                RoundedCornerShape(26.dp)
                        )
                ) {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                    ) {

                        Text(
                            text = "SUPER STAR",
                            color =
                                Color(0xFFFF8CFF),
                            fontSize = 12.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(3.dp)
                        )

                        Text(
                            text = "Live Event",
                            color = Color.White,
                            fontSize = 27.sp,
                            fontWeight =
                                FontWeight.ExtraBold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )

                        Text(
                            text =
                                "Join now & win amazing gifts",
                            color =
                                Color(0xFFE2D5EA),
                            fontSize = 12.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(13.dp)
                        )

                        Button(

                            onClick = {

                                if (rooms.isNotEmpty()) {

                                    navController.navigate(
                                        Screen.Room.createRoute(
                                            rooms.first().roomId
                                        )
                                    )
                                }
                            },

                            shape =
                                RoundedCornerShape(14.dp),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        Color(0xFFB63DFF)
                                )
                        ) {

                            Text(
                                text = "Join Now",
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }


                    Text(
                        text = "🎤",
                        fontSize = 62.sp,
                        modifier =
                            Modifier
                                .align(
                                    Alignment.CenterEnd
                                )
                                .padding(end = 28.dp)
                    )


                    Text(
                        text = "🎁",
                        fontSize = 30.sp,
                        modifier =
                            Modifier
                                .align(
                                    Alignment.BottomEnd
                                )
                                .padding(
                                    end = 30.dp,
                                    bottom = 15.dp
                                )
                    )
                }
            }


            // --------------------------------
            // CATEGORY
            // --------------------------------

            item {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    CategoryChip(
                        text = "All",
                        selected = true
                    )

                    CategoryChip(
                        text = "🔥 Hot"
                    )

                    CategoryChip(
                        text = "✨ New"
                    )

                    CategoryChip(
                        text = "🎵 Music"
                    )
                }
            }


            // --------------------------------
            // LIVE ROOM HEADER
            // --------------------------------

            item {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Row(
                        modifier =
                            Modifier.weight(1f),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Mic,

                            contentDescription =
                                null,

                            tint =
                                Color(0xFFCB63FF)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(7.dp)
                        )

                        Text(
                            text =
                                "Live Voice Rooms",

                            color =
                                Color.White,

                            fontSize = 20.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }


                    TextButton(
                        onClick = {

                            navController.navigate(
                                Screen.VoiceRooms.route
                            )
                        }
                    ) {

                        Text(
                            text = "View All →",
                            color =
                                Color(0xFFB55AFF)
                        )
                    }
                }
            }


            // --------------------------------
            // ROOMS
            // --------------------------------

            if (rooms.isEmpty()) {

                item {

                    EmptyRoomsCard(

                        onCreate = {

                            navController.navigate(
                                Screen.CreateVoiceRoom.route
                            )
                        }
                    )
                }

            } else {

                items(
                    items = rooms,
                    key = {
                        it.roomId
                    }
                ) { room ->

                    LiveRoomCard(

                        room = room,

                        onJoin = {

                            navController.navigate(
                                Screen.Room.createRoute(
                                    room.roomId
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}


// ========================================
// LIVE ROOM CARD
// ========================================

@Composable
private fun LiveRoomCard(
    room: VoiceRoomModel,
    onJoin: () -> Unit
) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape =
                    RoundedCornerShape(24.dp)
            ),

        shape =
            RoundedCornerShape(24.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFF171020)
            )
    ) {

        Column {

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
                    .background(
                        brush =
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF59217D),
                                    Color(0xFF25103D),
                                    Color(0xFF10101D)
                                )
                            )
                    )
            ) {

                Surface(

                    modifier =
                        Modifier.padding(10.dp),

                    shape =
                        RoundedCornerShape(10.dp),

                    color =
                        Color(0xFFEB3655)
                ) {

                    Text(
                        text = "🔥 LIVE",

                        modifier =
                            Modifier.padding(
                                horizontal = 9.dp,
                                vertical = 5.dp
                            ),

                        color = Color.White,

                        fontSize = 10.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }


                Text(
                    text = "🎤",
                    fontSize = 55.sp,

                    modifier =
                        Modifier.align(
                            Alignment.Center
                        )
                )


                Surface(

                    modifier =
                        Modifier
                            .align(
                                Alignment.TopEnd
                            )
                            .padding(10.dp),

                    shape =
                        RoundedCornerShape(12.dp),

                    color =
                        Color(0xAA130C1D)
                ) {

                    Row(
                        modifier =
                            Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 5.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Person,

                            contentDescription =
                                null,

                            tint =
                                Color(0xFF67F28D),

                            modifier =
                                Modifier.size(14.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(3.dp)
                        )

                        Text(
                            text =
                                "${room.users}",

                            color =
                                Color.White,

                            fontSize = 11.sp
                        )
                    }
                }
            }


            Column(
                modifier =
                    Modifier.padding(14.dp)
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "🎙 ${room.roomName}",

                            color =
                                Color.White,

                            fontSize = 17.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )

                        Text(
                            text =
                                "Host: ${room.hostName}",

                            color =
                                Color(0xFFB8A4C3),

                            fontSize = 12.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(
                                modifier =
                                    Modifier
                                        .size(7.dp)
                                        .background(
                                            Color(0xFF5AF28B),
                                            CircleShape
                                        )
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(5.dp)
                            )

                            Text(
                                text =
                                    "${room.users} Online",

                                color =
                                    Color(0xFF68EA91),

                                fontSize = 11.sp
                            )
                        }
                    }


                    Button(

                        onClick = onJoin,

                        shape =
                            RoundedCornerShape(13.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    Color(0xFF913BDE)
                            )
                    ) {

                        Text(
                            text = "JOIN",
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


// ========================================
// HEADER BUTTON
// ========================================

@Composable
private fun HomeHeaderButton(
    icon: ImageVector,
    onClick: () -> Unit
) {

    IconButton(

        onClick = onClick,

        modifier =
            Modifier
                .size(39.dp)
                .background(
                    Color(0xFF1B1125),
                    RoundedCornerShape(13.dp)
                )
    ) {

        Icon(
            imageVector = icon,

            contentDescription = null,

            tint = Color.White,

            modifier =
                Modifier.size(19.dp)
        )
    }
}


// ========================================
// CATEGORY CHIP
// ========================================

@Composable
private fun CategoryChip(
    text: String,
    selected: Boolean = false
) {

    Surface(

        shape =
            RoundedCornerShape(13.dp),

        color =
            if (selected)
                Color(0xFF8737C4)
            else
                Color(0xFF1A1022)
    ) {

        Text(

            text = text,

            modifier =
                Modifier.padding(
                    horizontal = 13.dp,
                    vertical = 8.dp
                ),

            color =
                if (selected)
                    Color.White
                else
                    Color(0xFFB7A5C1),

            fontSize = 11.sp,

            fontWeight =
                if (selected)
                    FontWeight.Bold
                else
                    FontWeight.Normal
        )
    }
}


// ========================================
// EMPTY ROOMS
// ========================================

@Composable
private fun EmptyRoomsCard(
    onCreate: () -> Unit
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFF171020)
            )
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(28.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "🎙️",
                fontSize = 45.sp
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = "No Live Rooms",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    "Be the first one to create a room.",

                color =
                    Color(0xFF9C899F),

                fontSize = 12.sp
            )

            Spacer(
                modifier =
                    Modifier.height(15.dp)
            )

            Button(

                onClick = onCreate,

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color(0xFF913BDE)
                    ),

                shape =
                    RoundedCornerShape(14.dp)
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Add,

                    contentDescription =
                        null
                )

                Spacer(
                    modifier =
                        Modifier.width(5.dp)
                )

                Text("Create Room")
            }
        }
    }
}


// ========================================
// BOTTOM NAVIGATION
// ========================================

@Composable
private fun HomeBottomBar(
    navController: NavController
) {

    NavigationBar(

        containerColor =
            Color(0xFF100A16),

        tonalElevation = 10.dp
    ) {

        NavigationBarItem(

            selected = true,

            onClick = {},

            icon = {

                Icon(
                    Icons.Default.Home,
                    contentDescription =
                        "Home"
                )
            },

            label = {
                Text("Home")
            }
        )


        NavigationBarItem(

            selected = false,

            onClick = {

                navController.navigate(
                    Screen.Search.route
                )
            },

            icon = {

                Icon(
                    Icons.Default.Explore,
                    contentDescription =
                        "Explore"
                )
            },

            label = {
                Text("Explore")
            }
        )


        NavigationBarItem(

            selected = false,

            onClick = {

                navController.navigate(
                    Screen.Chat.route
                )
            },

            icon = {

                Icon(
                    Icons.Default.ChatBubble,
                    contentDescription =
                        "Messages"
                )
            },

            label = {
                Text("Messages")
            }
        )


        NavigationBarItem(

            selected = false,

            onClick = {

                navController.navigate(
                    Screen.Games.route
                )
            },

            icon = {

                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription =
                        "Games"
                )
            },

            label = {
                Text("Games")
            }
        )


        NavigationBarItem(

            selected = false,

            onClick = {

                navController.navigate(
                    Screen.Profile.route
                )
            },

            icon = {

                Icon(
                    Icons.Default.Person,
                    contentDescription =
                        "Me"
                )
            },

            label = {
                Text("Me")
            }
        )
    }
}