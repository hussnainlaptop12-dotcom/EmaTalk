package com.example.ematalk.voice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ematalk.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceRoomScreen(
    navController: NavController
) {

    val repository = remember {
        VoiceRepository()
    }

    val rooms = remember {
        mutableStateListOf<VoiceRoomModel>()
    }

    LaunchedEffect(Unit) {

        repository.getRooms { result ->

            rooms.clear()
            rooms.addAll(result)
        }
    }

    Scaffold(

        containerColor = Color(0xFF08040F),

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        text = "Live Voice Rooms",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D0718)
                )
            )
        },

        floatingActionButton = {

            FloatingActionButton(

                onClick = {
                    navController.navigate(
                        Screen.CreateVoiceRoom.route
                    )
                },

                containerColor = Color(0xFF8B3DFF)
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Room",
                    tint = Color.White
                )
            }
        }

    ) { padding ->

        if (rooms.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "🎙️",
                        fontSize = 55.sp
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "No Live Rooms",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Create the first room!",
                        color = Color.Gray
                    )
                }
            }

        } else {

            LazyColumn(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(14.dp),

                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {

                items(
                    items = rooms,
                    key = {
                        it.roomId
                    }
                ) { room ->

                    Card(

                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                navController.navigate(
                                    Screen.Room.createRoute(
                                        room.roomId
                                    )
                                )
                            },

                        shape = RoundedCornerShape(22.dp),

                        colors = CardDefaults.cardColors(
                            containerColor =
                                Color(0xFF171021)
                        )
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(18.dp)
                        ) {

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(58.dp)
                                        .background(
                                            brush =
                                                Brush.radialGradient(
                                                    listOf(
                                                        Color(0xFFB85CFF),
                                                        Color(0xFF54208A)
                                                    )
                                                ),
                                            shape =
                                                RoundedCornerShape(18.dp)
                                        ),
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.Mic,
                                        contentDescription =
                                            null,
                                        tint = Color.White
                                    )
                                }

                                Spacer(
                                    modifier =
                                        Modifier.width(14.dp)
                                )

                                Column(
                                    modifier =
                                        Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = room.roomName,
                                        color = Color.White,
                                        fontSize = 18.sp,
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
                                            Color(0xFFB99BCB)
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(4.dp)
                                    )

                                    Row(
                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        Icon(
                                            imageVector =
                                                Icons.Default.Person,
                                            contentDescription =
                                                null,
                                            tint =
                                                Color(0xFF67E89A),
                                            modifier =
                                                Modifier.size(17.dp)
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.width(4.dp)
                                        )

                                        Text(
                                            text =
                                                "${room.users} Online",
                                            color =
                                                Color(0xFF67E89A)
                                        )
                                    }
                                }

                                Button(

                                    onClick = {

                                        navController.navigate(
                                            Screen.Room
                                                .createRoute(
                                                    room.roomId
                                                )
                                        )
                                    },

                                    shape =
                                        RoundedCornerShape(14.dp),

                                    colors =
                                        ButtonDefaults
                                            .buttonColors(
                                                containerColor =
                                                    Color(0xFF8B3DFF)
                                            )
                                ) {

                                    Text(
                                        text = "JOIN"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}