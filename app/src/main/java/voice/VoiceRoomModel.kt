package com.example.ematalk.voice

data class VoiceRoomModel(

    val roomId: String = "",
    val roomName: String = "",
    val hostId: String = "",
    val hostName: String = "",
    val users: Int = 0,
    val createdAt: Long = System.currentTimeMillis()

)