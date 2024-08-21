package com.ai.guildmasterapp.api

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlinx.serialization.Serializable

class GuildWars2Api {

    private val client = OkHttpClient()

    fun getCharacters(callback: (List<String>?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            fetchApiKey(uid) { apiKey ->
                fetchCharacters((apiKey).toString(), callback)
            }
        }
    }

    fun getCharacterDetails(characterId: String, callback: (List<CharacterDetail>?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            fetchApiKey(uid) { apiKey ->
                if (apiKey != null) {
                    fetchCharacterDetails(apiKey, characterId, callback)
                }
            }
        }
    }


    private fun fetchApiKey(uid: String, callback: (String?) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()

        val docRef = firestore.collection("users").document(uid)
        docRef.get()
            .addOnSuccessListener { document ->
                    val apiKey = document.getString("apiKey")
                    callback(apiKey)
            }
    }

    fun fetchCharacterDetails(apiKey: String, characterId: String, callback: (List<CharacterDetail>?) -> Unit) {
        val request = Request.Builder()
            .url("https://api.guildwars2.com/v2/characters?access_token=$apiKey&ids=$characterId")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let { jsonResponse ->
                    try {
                        if (jsonResponse.isNotEmpty()) {
                            val characterDetails = Json.decodeFromString<List<CharacterDetail>>(jsonResponse)
                            callback(characterDetails)
                        } else {
                            callback(getFallbackCharacterDetails())
                        }
                    } catch (e: IOException) {
                        Log.e("GuildWars2Api", "Invalid JSON format: ${e.message}")
                        callback(getFallbackCharacterDetails())
                    } catch (e: Exception) {
                        Log.e("GuildWars2Api", "Unexpected error: ${e.message}")
                        callback(getFallbackCharacterDetails())
                    }
                } ?: run {
                    callback(getFallbackCharacterDetails())
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("GuildWars2Api", "API call failed: ${e.message}")
                callback(getFallbackCharacterDetails())
            }
        })
    }

    private fun getFallbackCharacterDetails(): List<CharacterDetail> {
        return listOf(
            CharacterDetail(
                name = "Lopescodex",
                race = "Human",
                gender = "Male",
                profession = "Mesmer",
                level = 6,
                age = 3634,
                created = "2022-12-23T16:13:00Z",
                deaths = 0,
                equipment = listOf(
                    Equipment(id = 92960, slot = "Coat"),
                    Equipment(id = 6464, slot = "Boots", dyes = listOf(117, 19, 376, null)),
                    Equipment(id = 3339, slot = "Gloves"),
                    Equipment(id = 6553, slot = "Helm", dyes = listOf(19, 376, 19, null)),
                    Equipment(id = 3377, slot = "Leggings"),
                    Equipment(id = 23720, slot = "Accessory1"),
                    Equipment(id = 64670, slot = "WeaponA1"),
                    Equipment(id = 97730, slot = "FishingRod", binding = "Account")
                ),
                bags = listOf(
                    Bag(id = 8932, size = 20, inventory = listOf(
                        Item(id = 32138, count = 1, binding = "Character", bound_to = "Lopescodex"),
                        Item(id = 19552, count = 1, binding = "Character", bound_to = "Lopescodex"),
                        Item(id = 92917, count = 1, binding = "Account"),
                        Item(id = 26711, count = 1),
                        Item(id = 92899, count = 1, binding = "Account"),
                        Item(id = 98599, count = 1, binding = "Character", bound_to = "Lopescodex"),
                        Item(id = 12143, count = 2),
                        Item(id = 92936, count = 1, binding = "Character", bound_to = "Lopescodex"),
                        Item(id = 24359, count = 1),
                        Item(id = 19541, count = 1, binding = "Character", bound_to = "Lopescodex"),
                        Item(id = 24290, count = 1),
                        Item(id = 6467, count = 1, dyes = listOf(19, 376, 19, 1), binding = "Character", bound_to = "Lopescodex"),
                        Item(id = 19570, count = 3, binding = "Character", bound_to = "Lopescodex"),
                        Item(id = 24272, count = 1),
                        Item(id = 6465, count = 1, dyes = listOf(19, 19, 19, 1), binding = "Character", bound_to = "Lopescodex"),
                        Item(id = 24466, count = 1),
                        Item(id = 92917, count = 1, binding = "Character", bound_to = "Lopescodex"),
                        Item(id = 92899, count = 1, binding = "Account"),
                        Item(id = 24162, count = 1, binding = "Character", bound_to = "Lopescodex"),
                        Item(id = 33355, count = 1)
                    )),
                    null, null, null, null
                )
            )
        )
    }


    private fun fetchCharacters(apiKey: String, callback: (List<String>?) -> Unit) {
        val request = Request.Builder()
            .url("https://api.guildwars2.com/v2/characters?access_token=$apiKey")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let { jsonResponse ->
                    try {
                        if (jsonResponse.isNotEmpty()) {
                            val characters = Json.decodeFromString<List<String>>(jsonResponse)
                            callback(characters)
                        } else {
                            callback(listOf("lopescodex", "AI Squad"))
                        }
                    } catch (e: IOException) {
                        Log.e("GuildWars2Api", "Invalid JSON format: ${e.message}")
                    } catch (e: Exception) {
                        Log.e("GuildWars2Api", "Unexpected error: ${e.message}")
                        callback(listOf("lopescodex", "AI Squad"))
                    }
                } ?: run {
                    callback(listOf("lopescodex", "AI Squad"))
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("GuildWars2Api", "API call failed: ${e.message}")
                callback(listOf("lopescodex", "AI Squad"))
            }
        })
    }
}

@Serializable
data class CharacterDetail(
    val name: String,
    val race: String,
    val gender: String,
    val profession: String,
    val level: Int,
    val age: Int,
    val created: String,
    val deaths: Int,
    val equipment: List<Equipment>,
    val bags: List<Bag?>
)

@Serializable
data class Equipment(
    val id: Int,
    val slot: String,
    val binding: String? = null,
    val bound_to: String? = null,
    val dyes: List<Int?>? = null
)

@Serializable
data class Bag(
    val id: Int,
    val size: Int,
    val inventory: List<Item?>
)

@Serializable
data class Item(
    val id: Int,
    val count: Int,
    val binding: String? = null,
    val bound_to: String? = null,
    val dyes: List<Int?>? = null
)