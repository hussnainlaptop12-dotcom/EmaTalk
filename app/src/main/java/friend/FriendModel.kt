package com.example.ematalk.friend

data class FriendModel(

    val requestId: String = "",

    val senderId: String = "",

    val receiverId: String = "",

    val status: String = "pending"

)