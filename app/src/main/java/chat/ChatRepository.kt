package com.example.ematalk.chat

import com.google.firebase.firestore.FirebaseFirestore

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()

    fun sendMessage(
        message: MessageModel,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("messages")
            .document(message.messageId)
            .set(message)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getMessages(
        onResult: (List<MessageModel>) -> Unit
    ) {
        db.collection("messages")
            .orderBy("time")
            .addSnapshotListener { value, error ->

                if (error != null) {
                    return@addSnapshotListener
                }

                val list = ArrayList<MessageModel>()

                value?.documents?.forEach { doc ->
                    val message = doc.toObject(MessageModel::class.java)

                    if (message != null) {
                        list.add(message)
                    }
                }

                onResult(list)
            }
    }
}