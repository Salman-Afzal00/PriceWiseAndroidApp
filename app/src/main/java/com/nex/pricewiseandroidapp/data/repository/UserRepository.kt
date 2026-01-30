package com.nex.pricewiseandroidapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nex.pricewiseandroidapp.data.model.UserProfile

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    // Save user data (Call this after Sign Up or Google Login)
    fun saveUserToFirestore(user: UserProfile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(user.uid).set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Fetch user data (Call this when loading Profile Screen)
    fun getUserFromFirestore(uid: String, onSuccess: (UserProfile?) -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(UserProfile::class.java)
                    onSuccess(user)
                } else {
                    onSuccess(null) // User document doesn't exist
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }
}