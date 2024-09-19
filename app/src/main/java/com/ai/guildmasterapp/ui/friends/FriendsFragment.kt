package com.ai.guildmasterapp.ui.friends

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ai.guildmasterapp.R
import com.ai.guildmasterapp.api.FirebaseFirestoreDatabase

class FriendsFragment : Fragment() {

    private lateinit var editSearchEmail: EditText
    private lateinit var buttonSearch: Button
    private lateinit var friendsListContainer: LinearLayout
    private val firestoreDb = FirebaseFirestoreDatabase()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_friends, container, false)

        editSearchEmail = rootView.findViewById(R.id.edit_search_email)
        buttonSearch = rootView.findViewById(R.id.button_search)
        friendsListContainer = rootView.findViewById(R.id.friends_list_container)

        buttonSearch.setOnClickListener {
            val email = editSearchEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                searchForUserByEmail(email)
            }
        }

        loadFriendsList()

        return rootView
    }

    private fun loadFriendsList() {
        val currentUserId = firestoreDb.getCurrentUserId() // Fetch the current user ID
        if (currentUserId != null) {
            firestoreDb.getFriends(currentUserId) { friendsList ->
                if (friendsList.isNotEmpty()) {
                    // Populate the friends list in the LinearLayout
                    displayFriendsList(friendsList)
                }
            }
        }
    }

    private fun displayFriendsList(friendsList: List<String>) {
        friendsListContainer.removeAllViews()

        for ((index, friend) in friendsList.withIndex()) {
            val cardView = CardView(requireContext()).apply {
                radius = 8f
                cardElevation = 4f
                setContentPadding(16, 16, 16, 16)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 16, 16, 16) // Space between cards
                }
            }

            val itemLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 16, 16, 16)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val profileImageView = ImageView(requireContext()).apply {
                setImageResource(R.drawable.profile) // TODO CHANGE TO PROFILE IMAGE
                layoutParams = LinearLayout.LayoutParams(100, 100).apply {
                    setMargins(0, 0, 16, 0)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            val friendTextView = TextView(requireContext()).apply {
                text = friend
                textSize = 18f
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                setPadding(16, 16, 16, 16)
            }

            itemLayout.addView(profileImageView)
            itemLayout.addView(friendTextView)

            cardView.addView(itemLayout)

            friendsListContainer.addView(cardView)

            if (index < friendsList.size - 1) {
                val divider = View(requireContext()).apply {
                    setBackgroundColor(Color.LTGRAY)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        2
                    ).apply {
                        setMargins(16, 0, 16, 0)
                    }
                }
                friendsListContainer.addView(divider)
            }
        }
    }

    private fun searchForUserByEmail(email: String) {
        firestoreDb.searchUserByEmail(email) { result ->
            if (result != null && result.isNotEmpty()) {
                // Extract emails from the result and display in the list
                val foundUsers = result.mapNotNull { it["email"] as? String }
                displayFriendsList(foundUsers)
            } else {
                friendsListContainer.removeAllViews()
                val noResultsTextView = TextView(requireContext()).apply {
                    text = "No users found with email: $email"
                    textSize = 16f
                    setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                    setPadding(16, 16, 16, 16)
                }
                friendsListContainer.addView(noResultsTextView)
            }
        }
    }
}
