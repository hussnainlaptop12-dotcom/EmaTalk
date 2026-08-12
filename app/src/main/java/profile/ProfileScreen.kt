package com.example.ematalk.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(
    navController: NavController
) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var name by remember {
        mutableStateOf("EmaTalk User")
    }

    var username by remember {
        mutableStateOf("@ematalk_user")
    }

    var bio by remember {
        mutableStateOf("Welcome to EmaTalk")
    }

    var loading by remember {
        mutableStateOf(true)
    }

    val uid = auth.currentUser?.uid

    LaunchedEffect(uid) {

        if (uid != null) {

            db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->

                    name =
                        document.getString("name")
                            ?: auth.currentUser?.displayName
                                    ?: "EmaTalk User"

                    username =
                        document.getString("username")
                            ?.let { "@$it" }
                            ?: "@ematalk_user"

                    bio =
                        document.getString("bio")
                            ?: "Welcome to EmaTalk"

                    loading = false
                }
                .addOnFailureListener {

                    loading = false
                }
        } else {

            loading = false
        }
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
                .padding(20.dp)
        ) {

            // TOP BAR

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Profile",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        // Settings later
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            // PROFILE HEADER

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF9B4DFF),
                                    Color(0xFFFF4FD8)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text =
                            name.firstOrNull()
                                ?.uppercase()
                                ?: "E",

                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.width(18.dp)
                )

                Column {

                    if (loading) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFFB889FF)
                        )

                    } else {

                        Text(
                            text = name,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = username,
                            color = Color(0xFFB889FF),
                            fontSize = 14.sp
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = "● Online",
                            color = Color(0xFF55E68A),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // BIO

            Text(
                text = bio,
                color = Color(0xFFBDB0C8),
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // EDIT PROFILE

            Button(
                onClick = {
                    navController.navigate("edit_profile")
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),

                shape = RoundedCornerShape(16.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B3DFF)
                )
            ) {

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Edit Profile",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            // STATS

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                ProfileStat(
                    value = "12",
                    label = "Friends",
                    modifier = Modifier.weight(1f)
                )

                ProfileStat(
                    value = "248",
                    label = "Followers",
                    modifier = Modifier.weight(1f)
                )

                ProfileStat(
                    value = "36",
                    label = "Following",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // COINS

            ProfileMenuItem(
                icon = Icons.Default.Star,
                title = "Coins",
                subtitle = "500 Coins",
                onClick = {}
            )

            // GIFTS

            ProfileMenuItem(
                icon = Icons.Default.CardGiftcard,
                title = "My Gifts",
                subtitle = "View received gifts",
                onClick = {}
            )

            // VOICE ROOMS

            ProfileMenuItem(
                icon = Icons.Default.Mic,
                title = "My Voice Rooms",
                subtitle = "Manage your rooms",
                onClick = {
                    navController.navigate("voice_rooms")
                }
            )

            // FRIENDS

            ProfileMenuItem(
                icon = Icons.Default.People,
                title = "Friends",
                subtitle = "View your friends",
                onClick = {
                    navController.navigate("friends")
                }
            )

            // SETTINGS

            ProfileMenuItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                subtitle = "App settings and privacy",
                onClick = {}
            )
        }
    }
}

@Composable
private fun ProfileStat(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF211034))
            .padding(vertical = 15.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = value,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = label,
            color = Color(0xFFB889FF),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF171021))
            .clickable {
                onClick()
            }
            .padding(16.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Color(0xFF321650)),

            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFB889FF)
            )
        }

        Spacer(
            modifier = Modifier.width(15.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = subtitle,
                color = Color(0xFF8E8199),
                fontSize = 12.sp
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF75647F)
        )
    }
}