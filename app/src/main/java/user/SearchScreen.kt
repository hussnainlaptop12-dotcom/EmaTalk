package com.example.ematalk.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ematalk.model.UserModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.ematalk.friend.FriendRepository
import androidx.compose.material3.Button
@Composable
fun SearchScreen() {
    val context = LocalContext.current
    val friendRepository = remember { FriendRepository() }
    val repository = remember { UserRepository() }

    var search by remember { mutableStateOf("") }

    var users by remember {
        mutableStateOf<List<UserModel>>(emptyList())
    }

    LaunchedEffect(Unit) {

        repository.getAllUsers {

            users = it

        }

    }

    val filteredUsers = users.filter {

        it.name.contains(search, ignoreCase = true) ||
                it.username.contains(search, ignoreCase = true)

    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        OutlinedTextField(
            value = search,
            onValueChange = {
                search = it
            },
            label = {
                Text("Search User")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        LazyColumn {

            items(filteredUsers) { user ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )

                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = user.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "@${user.username}"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {

                                friendRepository.sendFriendRequest(
                                    toUid = user.uid,
                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            "Friend Request Sent",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onFailure = {
                                        Toast.makeText(
                                            context,
                                            it.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )

                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("➕ Add Friend")
                        }
                    }

                }

            }

        }

    }

}
