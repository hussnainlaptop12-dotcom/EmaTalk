package com.example.ematalk.voice

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ematalk.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

@Composable
fun CreateVoiceRoomScreen(
    navController: NavController
) {

    val context = LocalContext.current

    val repository = remember {
        VoiceRepository()
    }

    val auth = FirebaseAuth.getInstance()

    var roomName by remember {
        mutableStateOf("")
    }

    var creating by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF07030F),
                        Color(0xFF1A0735),
                        Color(0xFF08040F)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Text(
                text = "🎙️",
                fontSize = 65.sp
            )

            Text(
                text = "Create Voice Room",
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Start your own live room",
                color = Color(0xFFB889FF)
            )

            Spacer(
                modifier = Modifier.height(35.dp)
            )

            OutlinedTextField(
                value = roomName,
                onValueChange = {
                    roomName = it
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = {
                    Text("Room Name")
                },
                placeholder = {
                    Text("Friendship Room")
                },
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF9B4DFF),
                    unfocusedBorderColor = Color(0xFF654B79),
                    focusedLabelColor = Color(0xFFB889FF),
                    cursorColor = Color(0xFFB889FF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            Button(
                enabled =
                    roomName.isNotBlank() &&
                            !creating,

                onClick = {

                    val user = auth.currentUser

                    if (user == null) {

                        Toast.makeText(
                            context,
                            "Please login first",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    creating = true

                    val room = VoiceRoomModel(

                        roomId = UUID.randomUUID().toString(),

                        roomName = roomName.trim(),

                        hostId = user.uid,

                        hostName =
                            user.displayName
                                ?: user.email
                                ?: "Host",

                        users = 1
                    )

                    repository.createRoom(

                        room = room,

                        onSuccess = {

                            creating = false

                            Toast.makeText(
                                context,
                                "Room Created!",
                                Toast.LENGTH_SHORT
                            ).show()

                            // DIRECTLY OPEN CREATED ROOM
                            navController.navigate(
                                Screen.Room.createRoute(
                                    room.roomId
                                )
                            ) {

                                popUpTo(
                                    Screen.CreateVoiceRoom.route
                                ) {
                                    inclusive = true
                                }
                            }
                        },

                        onFailure = { error ->

                            creating = false

                            Toast.makeText(
                                context,
                                error.message
                                    ?: "Room creation failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),

                shape = RoundedCornerShape(18.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B3DFF)
                )
            ) {

                if (creating) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Text("Creating...")

                } else {

                    Text(
                        text = "CREATE ROOM",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}