package com.example.ematalk.room

import android.Manifest
import android.content.res.Configuration
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.livekit.android.LiveKit
import io.livekit.android.room.Room
import io.livekit.android.room.participant.RemoteParticipant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

private const val MAX_PARTICIPANTS = 5
private const val BACKEND_IP = "192.168.100.101"
private const val BACKEND_PORT = 3000
private const val PREFS_NAME = "ematalk_room_prefs"
private const val IDENTITY_KEY = "persistent_identity"

@Composable
fun RoomScreen(
    roomId: String,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Same identity survives app close/reopen.
    val savedIdentity = remember {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.getString(IDENTITY_KEY, null) ?: run {
            val newIdentity = "user_" + java.util.UUID.randomUUID().toString().take(8)
            prefs.edit().putString(IDENTITY_KEY, newIdentity).apply()
            newIdentity
        }
    }

    var connecting by remember { mutableStateOf(true) }
    var connected by remember { mutableStateOf(false) }
    var isMicOn by remember { mutableStateOf(false) }
    var remoteList by remember { mutableStateOf<List<RemoteParticipant>>(emptyList()) }

    val room = remember {
        LiveKit.create(context.applicationContext)
    }

    LaunchedEffect(connected) {
        while (isActive && connected) {
            remoteList = room.remoteParticipants.values
                .take(MAX_PARTICIPANTS - 1)
            delay(500)
        }

        if (!connected) {
            remoteList = emptyList()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            connecting = false
            Toast.makeText(
                context,
                "Microphone permission is required",
                Toast.LENGTH_LONG
            ).show()
            return@rememberLauncherForActivityResult
        }

        scope.launch {
            var lastError = "LiveKit connection failed."

            repeat(3) { attempt ->
                if (!connected) {
                    connecting = true

                    connectToLiveKit(
                        room = room,
                        roomId = roomId,
                        identity = savedIdentity,
                        onConnected = {
                            connected = true
                            connecting = false
                            isMicOn = true
                        },
                        onError = { message ->
                            lastError = message
                        }
                    )

                    if (!connected && attempt < 2) {
                        delay(1500)
                    }
                }
            }

            if (!connected) {
                connecting = false
                isMicOn = false
                Toast.makeText(
                    context,
                    lastError,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    LaunchedEffect(roomId) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    DisposableEffect(Unit) {
        onDispose {
            room.disconnect()
            room.release()
        }
    }

    val totalParticipants = if (connected) {
        minOf(1 + remoteList.size, MAX_PARTICIPANTS)
    } else {
        0
    }

    val remoteParticipants = remoteList.take(4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF05020A),
                        Color(0xFF160521),
                        Color(0xFF08030F)
                    )
                )
            )
    ) {
        RoomTopBar(
            roomId = roomId,
            count = totalParticipants,
            onBack = {
                room.disconnect()
                navController.popBackStack()
            }
        )

        Text(
            text = "EmaTalk Voice Room",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Text(
            text = when {
                connecting -> "Connecting..."
                connected -> "● LIVE  •  $totalParticipants/$MAX_PARTICIPANTS"
                else -> "Connection failed"
            },
            color = when {
                connecting -> Color(0xFFFFC857)
                connected -> Color(0xFF65F59A)
                else -> Color(0xFFFF5570)
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = if (isLandscape) 6.dp else 8.dp)
        ) {
            RoomFloorGlow()

            if (isLandscape) {
                // Landscape: keep all 5 seats visible in one horizontal row.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LandscapeSeat(
                        seatNumber = 2,
                        participant = remoteParticipants.getOrNull(0)
                    )
                    LandscapeSeat(
                        seatNumber = 3,
                        participant = remoteParticipants.getOrNull(1)
                    )
                    LandscapeSeat(
                        seatNumber = 1,
                        participant = null,
                        isLocal = true,
                        localName = if (connected) {
                            room.localParticipant.name
                                ?.takeIf { it.isNotBlank() } ?: "You"
                        } else "You",
                        localMicOn = isMicOn,
                        localEmpty = !connected
                    )
                    LandscapeSeat(
                        seatNumber = 4,
                        participant = remoteParticipants.getOrNull(2)
                    )
                    LandscapeSeat(
                        seatNumber = 5,
                        participant = remoteParticipants.getOrNull(3)
                    )
                }

                if (connected && isMicOn) {
                    VoiceRings(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                // Portrait: keep the original 3D room arrangement.
                RoomSeat(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 5.dp),
                    seatNumber = 1,
                    name = if (connected) {
                        room.localParticipant.name
                            ?.takeIf { it.isNotBlank() } ?: "You"
                    } else "You",
                    subtitle = if (connected) "HOST • LIVE" else "HOST",
                    micOn = isMicOn,
                    isLocal = true,
                    empty = !connected
                )

                RoomSeat(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 18.dp, y = 36.dp),
                    seatNumber = 2,
                    name = remoteParticipants.getOrNull(0)?.displayNameOrGuest() ?: "Seat 2",
                    subtitle = if (remoteParticipants.getOrNull(0) != null) "PARTICIPANT • LIVE" else "EMPTY • WAITING",
                    micOn = remoteParticipants.getOrNull(0)?.isMicrophoneEnabled == true,
                    isLocal = false,
                    empty = remoteParticipants.getOrNull(0) == null
                )

                RoomSeat(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-18).dp, y = 36.dp),
                    seatNumber = 3,
                    name = remoteParticipants.getOrNull(1)?.displayNameOrGuest() ?: "Seat 3",
                    subtitle = if (remoteParticipants.getOrNull(1) != null) "PARTICIPANT • LIVE" else "EMPTY • WAITING",
                    micOn = remoteParticipants.getOrNull(1)?.isMicrophoneEnabled == true,
                    isLocal = false,
                    empty = remoteParticipants.getOrNull(1) == null
                )

                RoomSeat(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 18.dp, y = (-42).dp),
                    seatNumber = 4,
                    name = remoteParticipants.getOrNull(2)?.displayNameOrGuest() ?: "Seat 4",
                    subtitle = if (remoteParticipants.getOrNull(2) != null) "PARTICIPANT • LIVE" else "EMPTY • WAITING",
                    micOn = remoteParticipants.getOrNull(2)?.isMicrophoneEnabled == true,
                    isLocal = false,
                    empty = remoteParticipants.getOrNull(2) == null
                )

                RoomSeat(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-18).dp, y = (-42).dp),
                    seatNumber = 5,
                    name = remoteParticipants.getOrNull(3)?.displayNameOrGuest() ?: "Seat 5",
                    subtitle = if (remoteParticipants.getOrNull(3) != null) "PARTICIPANT • LIVE" else "EMPTY • WAITING",
                    micOn = remoteParticipants.getOrNull(3)?.isMicrophoneEnabled == true,
                    isLocal = false,
                    empty = remoteParticipants.getOrNull(3) == null
                )

                if (connected && isMicOn) {
                    VoiceRings(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        RoomBottomControls(
            connected = connected,
            micOn = isMicOn,
            onMicClick = {
                scope.launch {
                    try {
                        val newState = !isMicOn
                        val success = room.localParticipant
                            .setMicrophoneEnabled(newState)

                        if (success) {
                            isMicOn = newState
                        } else {
                            Toast.makeText(
                                context,
                                "Could not change microphone",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            e.message ?: "Microphone error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            },
            onChatClick = {
                Toast.makeText(
                    context,
                    "Chat coming next",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onLeaveClick = {
                room.disconnect()
                navController.popBackStack()
            }
        )
    }
}


@Composable
private fun LandscapeSeat(
    seatNumber: Int,
    participant: RemoteParticipant?,
    isLocal: Boolean = false,
    localName: String = "You",
    localMicOn: Boolean = false,
    localEmpty: Boolean = true
) {
    val empty = if (isLocal) localEmpty else participant == null
    val name = if (isLocal) {
        localName
    } else {
        participant?.displayNameOrGuest() ?: "Seat $seatNumber"
    }
    val micOn = if (isLocal) localMicOn else participant?.isMicrophoneEnabled == true
    val subtitle = when {
        empty && isLocal -> "HOST"
        empty -> "EMPTY • WAITING"
        isLocal -> "HOST • LIVE"
        else -> "PARTICIPANT • LIVE"
    }

    Card(
        modifier = Modifier
            .width(116.dp)
            .height(112.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                empty -> Color(0xB20F0918)
                isLocal -> Color(0xD52B1242)
                else -> Color(0xC9191026)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (empty) 4.dp else 12.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = when {
                    empty -> Color(0xFF24162E)
                    isLocal -> Color(0xFF8B32C6)
                    else -> Color(0xFF67279A)
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (empty) "＋" else name.take(1).uppercase(),
                        color = if (empty) Color(0xFF806F8B) else Color.White,
                        fontSize = if (empty) 22.sp else 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            Text(
                text = if (empty) "Seat $seatNumber" else name,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = subtitle,
                    color = if (empty) Color(0xFF776A80) else Color(0xFFB69CC5),
                    fontSize = 7.sp,
                    maxLines = 1
                )
                if (!empty) {
                    Spacer(Modifier.width(4.dp))
                    Text(if (micOn) "🎙" else "🔇", fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
private fun RoomTopBar(
    roomId: String,
    count: Int,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clickableNoRipple(onClick = onBack),
            shape = CircleShape,
            color = Color(0xFF24112F),
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "‹",
                    color = Color.White,
                    fontSize = 30.sp
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = "Voice Room",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Room ID: ${roomId.take(20)}",
                color = Color(0xFF9C8AA8),
                fontSize = 10.sp
            )
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF24112F),
            shadowElevation = 7.dp
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("👥", fontSize = 13.sp)
                Spacer(Modifier.width(5.dp))
                Text(
                    "$count/$MAX_PARTICIPANTS",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RoomFloorGlow() {
    Surface(
        modifier = Modifier.size(260.dp),
        shape = CircleShape,
        color = Color(0x181A0630)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.size(190.dp),
                shape = CircleShape,
                color = Color(0x121D0834)
            ) {}
        }
    }
}

@Composable
private fun RoomSeat(
    modifier: Modifier,
    seatNumber: Int,
    name: String,
    subtitle: String,
    micOn: Boolean,
    isLocal: Boolean,
    empty: Boolean
) {
    Card(
        modifier = modifier
            .width(142.dp)
            .height(128.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (empty) {
                Color(0xB20F0918)
            } else if (isLocal) {
                Color(0xD52B1242)
            } else {
                Color(0xC9191026)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (empty) 5.dp else 14.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            ) {
                if (isLocal && !empty) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(42.dp)
                            .height(4.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFB64BEF)
                    ) {}
                }
            }

            Surface(
                modifier = Modifier.size(53.dp),
                shape = CircleShape,
                color = if (empty) {
                    Color(0xFF24162E)
                } else if (isLocal) {
                    Color(0xFF8B32C6)
                } else {
                    Color(0xFF67279A)
                },
                shadowElevation = if (empty) 0.dp else 12.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (empty) "＋" else name.take(1).uppercase(),
                        color = if (empty) Color(0xFF806F8B) else Color.White,
                        fontSize = if (empty) 25.sp else 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (empty) "Seat $seatNumber" else name,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subtitle,
                    color = if (empty) Color(0xFF776A80) else Color(0xFFB69CC5),
                    fontSize = 8.sp,
                    maxLines = 1
                )

                if (!empty) {
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = if (micOn) "🎙" else "🔇",
                        fontSize = 10.sp
                    )
                }
            }

            if (isLocal && !empty) {
                Spacer(Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(7.dp),
                    color = Color(0xFF7B2FB3)
                ) {
                    Text(
                        text = "HOST",
                        color = Color.White,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(
                            horizontal = 7.dp,
                            vertical = 3.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun VoiceRings(modifier: Modifier) {
    val infinite = rememberInfiniteTransition(label = "voice_rings")
    val pulse by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .size((180 * pulse).dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(150.dp),
            shape = CircleShape,
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(
                2.dp,
                Color(0x447C32B5)
            )
        ) {}

        Surface(
            modifier = Modifier.size(122.dp),
            shape = CircleShape,
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(
                2.dp,
                Color(0x337C32B5)
            )
        ) {}
    }
}

@Composable
private fun RoomBottomControls(
    connected: Boolean,
    micOn: Boolean,
    onMicClick: () -> Unit,
    onChatClick: () -> Unit,
    onLeaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                enabled = connected,
                onClick = onMicClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (micOn) {
                        Color(0xFF7C32B5)
                    } else {
                        Color(0xFF2B1B35)
                    },
                    disabledContainerColor = Color(0xFF1A111F)
                )
            ) {
                Text(
                    if (micOn) "🎙  Mic On" else "🔇  Mic Off",
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onChatClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2B1B35)
                )
            ) {
                Text("💬  Chat", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onLeaveClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(17.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD82F4F)
            )
        ) {
            Text(
                "Leave Room",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun RemoteParticipant.displayNameOrGuest(): String {
    return name
        ?.takeIf { it.isNotBlank() }
        ?: identity?.value
        ?: "Guest"
}

@Composable
private fun Modifier.clickableNoRipple(
    onClick: () -> Unit
): Modifier {
    return this.then(
        Modifier
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    )
}

private suspend fun connectToLiveKit(
    room: Room,
    roomId: String,
    identity: String,
    onConnected: () -> Unit,
    onError: (String) -> Unit
) {
    try {
        if (roomId.isBlank()) {
            withContext(Dispatchers.Main) {
                onError("Room ID is empty.")
            }
            return
        }

        val backendUrl =
            "http://$BACKEND_IP:$BACKEND_PORT/token" +
                    "?room=" +
                    URLEncoder.encode(roomId, "UTF-8") +
                    "&identity=" +
                    URLEncoder.encode(identity, "UTF-8")

        val response = withContext(Dispatchers.IO) {
            val connection =
                URL(backendUrl).openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            try {
                val code = connection.responseCode

                val stream =
                    if (code in 200..299) {
                        connection.inputStream
                    } else {
                        connection.errorStream
                    }

                val text =
                    stream?.bufferedReader()?.use { it.readText() }
                        ?: "Backend returned no response"

                if (code !in 200..299) {
                    throw Exception("Backend error $code: $text")
                }

                text
            } finally {
                connection.disconnect()
            }
        }

        val json = JSONObject(response)

        val token = json.optString("token")
        val liveKitUrl = json.optString("url")

        if (token.isBlank()) {
            throw Exception("Backend did not return a token.")
        }

        if (liveKitUrl.isBlank()) {
            throw Exception("Backend did not return LIVEKIT_URL.")
        }

        room.connect(liveKitUrl, token)

        val microphoneStarted =
            room.localParticipant.setMicrophoneEnabled(true)

        withContext(Dispatchers.Main) {
            if (microphoneStarted) {
                onConnected()
            } else {
                onError(
                    "Room connected, but microphone could not start."
                )
            }
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            onError(
                e.message ?: "LiveKit connection failed."
            )
        }
    }
}