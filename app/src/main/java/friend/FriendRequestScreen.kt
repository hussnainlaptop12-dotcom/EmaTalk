package com.example.ematalk.friend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ematalk.model.FriendRequestModel

@Composable
fun FriendRequestScreen() {

    val repository = remember { FriendRepository() }

    var requests by remember {
        mutableStateOf<List<FriendRequestModel>>(emptyList())
    }

    LaunchedEffect(Unit) {
        repository.getPendingRequests {
            requests = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Friend Requests",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (requests.isEmpty()) {

            Text("No Friend Requests")

        } else {

            LazyColumn {

                items(requests) { request ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "From UID:",
                                fontWeight = FontWeight.Bold
                            )

                            Text(request.fromUid)

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    repository.acceptFriendRequest(
                                        fromUid = request.fromUid,
                                        onSuccess = {
                                            repository.getPendingRequests {
                                                requests = it
                                            }
                                        }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Text("✅ Accept")

                            }

                        }

                    }

                }

            }

        }

    }

}