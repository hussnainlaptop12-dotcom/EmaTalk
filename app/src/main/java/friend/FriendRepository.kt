package com.example.ematalk.friend

import com.example.ematalk.model.FriendRequestModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FriendRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Send Friend Request
    fun sendFriendRequest(
        toUid: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val fromUid = auth.currentUser?.uid ?: return

        val request = FriendRequestModel(
            fromUid = fromUid,
            toUid = toUid,
            status = "pending",
            timestamp = System.currentTimeMillis()
        )

        db.collection("friend_requests")
            .add(request)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }

    }

    // Get Pending Friend Requests
    fun getPendingRequests(
        onResult: (List<FriendRequestModel>) -> Unit
    )
    {

        val uid = auth.currentUser?.uid ?: return

        db.collection("friend_requests")
            .whereEqualTo("toUid", uid)
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { result ->

                val list = ArrayList<FriendRequestModel>()

                for (doc in result.documents) {

                    val request =
                        doc.toObject(FriendRequestModel::class.java)

                    if (request != null) {
                        list.add(request)
                    }

                }

                onResult(list)

            }

    }
    // Accept Friend Request
    fun acceptFriendRequest(
        fromUid: String,
        onSuccess: () -> Unit
    ) {

        val currentUid = auth.currentUser?.uid ?: return

        val friend = hashMapOf(
            "uid" to fromUid
        )

        val myFriend = hashMapOf(
            "uid" to currentUid
        )

        db.collection("users")
            .document(currentUid)
            .collection("friends")
            .document(fromUid)
            .set(friend)
            .addOnSuccessListener {

                db.collection("users")
                    .document(fromUid)
                    .collection("friends")
                    .document(currentUid)
                    .set(myFriend)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                fun deleteFriendRequest(
                    fromUid: String,
                    onSuccess: () -> Unit
                ) {

                    val currentUid = auth.currentUser?.uid ?: return

                    db.collection("friend_requests")
                        .whereEqualTo("fromUid", fromUid)
                        .whereEqualTo("toUid", currentUid)
                        .whereEqualTo("status", "pending")
                        .get()
                        .addOnSuccessListener { result ->

                            val batch = db.batch()

                            for (doc in result.documents) {
                                batch.delete(doc.reference)
                            }

                            batch.commit()
                                .addOnSuccessListener {
                                    onSuccess()
                                }

                        }

                }
            }

    }
    fun deleteFriendRequest(
        fromUid: String,
        onSuccess: () -> Unit
    ) {

        val currentUid = auth.currentUser?.uid ?: return

        db.collection("friend_requests")
            .whereEqualTo("fromUid", fromUid)
            .whereEqualTo("toUid", currentUid)
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { result ->

                val batch = db.batch()

                for (doc in result.documents) {
                    batch.delete(doc.reference)
                }

                batch.commit().addOnSuccessListener {
                    onSuccess()
                }
            }
    }
}