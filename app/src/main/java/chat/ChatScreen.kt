package com.example.ematalk.chat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController) {

    val context = LocalContext.current
    val repository = remember { ChatRepository() }
    val auth = FirebaseAuth.getInstance()

    var message by remember { mutableStateOf("") }

    val messages = remember {
        mutableStateListOf<MessageModel>()
    }

    LaunchedEffect(Unit) {

        repository.getMessages {

            messages.clear()
            messages.addAll(it)

        }

    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Private Chat",
                        fontWeight = FontWeight.Bold
                    )

                }

            )

        },

        bottomBar = {

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),

                verticalAlignment = Alignment.CenterVertically

            ) {

                OutlinedTextField(

                    value = message,

                    onValueChange = {

                        message = it

                    },

                    modifier = Modifier.weight(1f),

                    placeholder = {

                        Text("Type message")

                    }

                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(

                    onClick = {

                        if (message.isEmpty()) {

                            Toast.makeText(
                                context,
                                "Enter Message",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            val newMessage = MessageModel(

                                messageId = UUID.randomUUID().toString(),

                                senderId = auth.currentUser?.uid ?: "",

                                receiverId = "demoUser",

                                message = message

                            )
                            repository.sendMessage(

                                message = newMessage,

                                onSuccess = {
                                    message = ""
                                },

                                onFailure = {

                                    Toast.makeText(
                                        context,
                                        it.message ?: "Error",
                                        Toast.LENGTH_LONG
                                    ).show()

                                }

                            )

                        }

                    }

                ) {

                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send"
                    )

                }

            }

        }

    ) { padding ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(10.dp),

            reverseLayout = false

        ) {

            items(messages) { msg ->

                val isMe = msg.senderId == auth.currentUser?.uid

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        if (isMe)
                            Arrangement.End
                        else
                            Arrangement.Start

                ) {

                    Card(

                        shape = RoundedCornerShape(12.dp),

                        colors = CardDefaults.cardColors(

                            containerColor =
                                if (isMe)
                                    Color(0xFFDCF8C6)
                                else
                                    Color.White

                        )

                    ) {

                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {

                            Text(
                                text = msg.message
                            )

                        }

                    }

                }

                Spacer(modifier = Modifier.height(8.dp))

            }

        }

    }

}