package com.example.ematalk.room

import com.google.firebase.firestore.FirebaseFirestore

class RoomRepository {

    private val db = FirebaseFirestore.getInstance()

    fun createRoom(
        room: RoomModel,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        db.collection("rooms")
            .document(room.roomId)
            .set(room)
            .addOnSuccessListener {

                onSuccess()

            }
            .addOnFailureListener {

                onFailure(it)

            }

    }

    fun getRooms(onResult: (List<RoomModel>) -> Unit) {

        db.collection("rooms")
            .get()
            .addOnSuccessListener { result ->

                val list = ArrayList<RoomModel>()

                for (doc in result) {

                    val room = doc.toObject(RoomModel::class.java)
                    list.add(room)

                }

                onResult(list)

            }

    }

}
