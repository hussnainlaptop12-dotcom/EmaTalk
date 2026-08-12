package com.example.ematalk.room

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

@Composable
fun CreateRoomScreen(
    navController: NavController
) {

    var roomName by remember {
        mutableStateOf("")
    }

    var category by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var roomType by remember {
        mutableStateOf("Public")
    }

    var maxMembers by remember {
        mutableStateOf("10")
    }

    val context = LocalContext.current

    val repository = remember {
        RoomRepository()
    }

    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        Text(
            text = "Create Voice Room",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        OutlinedTextField(
            value = roomName,
            onValueChange = {
                roomName = it
            },
            label = {
                Text("Room Name")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = category,
            onValueChange = {
                category = it
            },
            label = {
                Text("Category")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
            },
            label = {
                Text("Description")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = roomType,
            onValueChange = {
                roomType = it
            },
            label = {
                Text("Room Type")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = maxMembers,
            onValueChange = {
                maxMembers = it
            },
            label = {
                Text("Max Members")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Button(
            onClick = {

                if (roomName.isBlank() || category.isBlank()) {

                    Toast.makeText(
                        context,
                        "Fill Room Name and Category",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    val room = RoomModel(
                        roomId = UUID.randomUUID().toString(),
                        roomName = roomName,
                         hostId = auth.currentUser?.uid ?: "",
                        hostName = auth.currentUser?.email ?: "",
                        category = category,
                        description = description,
                        roomType = roomType,
                        maxMembers = maxMembers.toIntOrNull() ?: 10
                    )

                    repository.createRoom(

                        room = room,

                        onSuccess = {

                            Toast.makeText(
                                context,
                                "Room Created",
                                Toast.LENGTH_SHORT
                            ).show()

                            navController.popBackStack()
                        },

                        onFailure = { error ->

                            Toast.makeText(
                                context,
                                error.message ?: "Room creation failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }
            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Create Room")
        }
    }
}
