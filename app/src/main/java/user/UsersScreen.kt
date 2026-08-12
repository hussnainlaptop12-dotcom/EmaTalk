package com.example.ematalk.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ematalk.model.UserModel

@Composable
fun UsersScreen() {

    val repository = remember { UserRepository() }

    var users by remember {
        mutableStateOf<List<UserModel>>(emptyList())
    }

    LaunchedEffect(Unit) {

        repository.getAllUsers {

            users = it

        }

    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        items(users) { user ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                )

            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = user.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = user.email
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = user.bio
                    )

                }

            }

        }

    }

}