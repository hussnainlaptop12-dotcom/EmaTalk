package com.example.ematalk.user

import com.example.ematalk.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    private val db = FirebaseFirestore.getInstance()

    fun saveUser(
        user: UserModel,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        db.collection("users")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }

    }
    fun getAllUsers(
        onResult: (List<UserModel>) -> Unit
    ) {

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->

                val list = ArrayList<UserModel>()

                for (document in result.documents) {

                    val user =
                        document.toObject(UserModel::class.java)

                    if (user != null) {
                        list.add(user)
                    }

                }

                onResult(list)

            }

    }
}