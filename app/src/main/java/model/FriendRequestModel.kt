package com.example.ematalk.model

data class FriendRequestModel(

    var id: String = "",

    val fromUid: String = "",

    val toUid: String = "",

    val status: String = "",

    val timestamp: Long = 0

)