package com.ai.guildmasterapp.api

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class FirebaseFirestoreDatabase {

    private val firestore = FirebaseFirestore.getInstance() // Firestore instance

    fun searchUserByEmail(query: String, callback: (List<Map<String, Any>>?) -> Unit) {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                val matchingUsers = documents.mapNotNull { document ->
                    val email = document.getString("email")?.lowercase() ?: ""
                    if (email.contains(query.lowercase())) {
                        document.data
                    } else {
                        null
                    }
                }

                if (matchingUsers.isNotEmpty()) {
                    callback(matchingUsers)
                } else {
                    callback(null)
                }
            }
    }


    // Function to get the list of friends for a user (unchanged)
    fun getFriends(userId: String, callback: (List<String>) -> Unit) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val friendsList = document.get("friends") as? List<String> ?: emptyList()
                    callback(friendsList)
                } else {
                    callback(emptyList())
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching friends list: ${e.message}")
                callback(emptyList())
            }
    }

    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}
