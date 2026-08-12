package com.example.ematalk.chat

data class MessageModel(

    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val chatId: String = "",
    val message: String = "",
    val time: Long = System.currentTimeMillis(),
    val seen: Boolean = false

)