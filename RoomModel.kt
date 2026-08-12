package com.example.ematalk.room

data class RoomModel(

    val roomId: String = "",
    val roomName: String = "",
val hostId: String = "",    val hostName: String = "",
    val category: String = "",
    val description: String = "",
    val roomType: String = "Public",
    val maxMembers: Int = 10,
    val members: Int = 1,
    val createdAt: Long = System.currentTimeMillis()

)
