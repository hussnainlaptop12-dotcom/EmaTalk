package com.example.ematalk.voice

import com.google.firebase.firestore.FirebaseFirestore

class VoiceRepository {

    private val db = FirebaseFirestore.getInstance()

    fun createRoom(
        room: VoiceRoomModel,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        db.collection("voice_rooms")
            .document(room.roomId)
            .set(room)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }

    }

    fun getRooms(
        onResult: (List<VoiceRoomModel>) -> Unit
    ) {

        db.collection("voice_rooms")
            .addSnapshotListener { value, error ->

                if (error != null) return@addSnapshotListener

                val list = ArrayList<VoiceRoomModel>()

                value?.documents?.forEach { doc ->

                    val room = doc.toObject(VoiceRoomModel::class.java)

                    if (room != null) {
                        list.add(room)
                    }

                }

                onResult(list)

            }

    }

}