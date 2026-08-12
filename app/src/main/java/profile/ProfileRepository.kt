package com.example.ematalk.profile

import com.example.ematalk.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUser(
        onSuccess: (UserModel) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                val user = document.toObject(UserModel::class.java)

                if (user != null) {
                    onSuccess(user)
                }

            }
            .addOnFailureListener {
                onFailure(it)
            }

    }

    fun logout() {
        auth.signOut()
    }

}