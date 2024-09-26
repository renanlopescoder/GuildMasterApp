package com.ai.guildmasterapp.api

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class FirebaseFirestoreDatabase {

    private val firestore = FirebaseFirestore.getInstance() // Firestore instance

    fun searchUserByEmail(query: String, userId: String, callback: (List<Map<String, Any>>?) -> Unit) {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                firestore.collection("users").document(userId).get().addOnSuccessListener { userDoc ->
                    val currentUserFriends = userDoc.get("friends") as? List<String> ?: emptyList()

                    val matchingUsers = documents.mapNotNull { document ->
                        val email = document.getString("email")?.lowercase() ?: ""
                        val foundUserId = document.id
                        val isFriend = currentUserFriends.contains(foundUserId)

                        if (email.contains(query.lowercase())) {
                            document.data.apply {
                                put("isFriend", isFriend)
                                put("userId", foundUserId)
                            }
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
    }


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

    fun addFriend(currentUserId: String, friendEmail: String, callback: (Boolean, String?) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("email", friendEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val friendEmailDoc = friendEmail
                    getFriends(currentUserId) { friendsList ->
                        if (friendsList.contains(friendEmailDoc)) {
                            callback(false, "User is already a friend.")
                        } else {
                            val updatedFriendsList = friendsList.toMutableList().apply {
                                add(friendEmailDoc)
                            }

                            firestore.collection("users").document(currentUserId)
                                .update("friends", updatedFriendsList)
                                .addOnSuccessListener {
                                    callback(true, null)
                                }
                        }
                    }
                } else {
                    callback(false, "User not found")
                }
            }
    }
}
