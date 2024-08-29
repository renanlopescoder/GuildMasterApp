package com.ai.guildmasterapp.api

import android.util.Log
import com.ai.guildmasterapp.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.resumeWithException

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

    fun getCharacterDetails(characterId: String, callback: (CharacterDetail?) -> Unit) {
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

    fun fetchCharacterDetails(apiKey: String, characterId: String, callback: (CharacterDetail?) -> Unit) {
        val request = Request.Builder()
            .url("https://api.guildwars2.com/v2/characters?access_token=$apiKey&ids=$characterId")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonResponse ->
                    try {
                        if (jsonResponse.isNotEmpty()) {
                            val characterDetails = Json.decodeFromString<List<CharacterDetail>>(jsonResponse)[0]
                            callback(characterDetails)
                            GlobalState.characterDetail = characterDetails
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

            override fun onFailure(call: Call, e: IOException) {
                Log.e("GuildWars2Api", "API call failed: ${e.message}")
                callback(getFallbackCharacterDetails())
            }
        })
    }

    private fun getFallbackCharacterDetails(): CharacterDetail {
        val char =  CharacterDetail(
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

        GlobalState.characterDetail = char
        return char
    }
    
    
    private fun getFallbackGuildInfo(): GuildInfo {
        
        val char =  GuildInfo(
            guild_id = "8774BBE4-25F8-4515-8557-D7BDE72A7F8A",
            guild_name = "Team Aggression",
            tag = "TA",
            emblem = Emblem(
                background_id = 2,
                foreground_id = 53,
                flags = listOf(""),
                background_color_id = 673,
                foreground_primary_color_id = 473,
                foreground_secondary_color_id = 443
            )
        )

        GlobalState.guildInfo = char
        return char
    }


    fun getFallbackEmblemLayer(type: String): EmblemLayer {

        lateinit var layer: EmblemLayer
        when (type) {
            "backgrounds" -> layer = EmblemLayer(
                id = 2,
                layers = listOf("https://render.guildwars2.com/file/936BEB492B0D2BD77307FCB10DBEE51AFB5E6C64/59599.png")
            )

            "foregrounds" -> layer = EmblemLayer(
                id = 53,
                layers = listOf(
                    "https://render.guildwars2.com/file/E8AB2107615E4717FE7E52CB686A0AC2AC5A4275/59955.png",
                    "https://render.guildwars2.com/file/FB6B1500A00C51312A7356551D40635D4372A1B2/59957.png",
                    "https://render.guildwars2.com/file/F81C9299DEB7C46F3B70F83A18AF2868DA34973C/59959.png"
                )
            )
        }

        return layer
    }


    private fun fetchCharacters(apiKey: String, callback: (List<String>?) -> Unit) {
        val request = Request.Builder()
            .url("https://api.guildwars2.com/v2/characters?access_token=$apiKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonResponse ->
                    try {
                        if (jsonResponse.isNotEmpty()) {
                            val characters = Json.decodeFromString<List<String>>(jsonResponse)
                            callback(characters)
                            GlobalState.characters = characters
                        } else {
                            callback(listOf("lopescodex", "AI Squad"))
                        }
                    } catch (e: IOException) {
                        Log.e("GuildWars2Api", "Invalid JSON format: ${e.message}")
                    } catch (e: Exception) {
                        Log.e("GuildWars2Api", "Unexpected error: ${e.message}")
                        callback(listOf("lopescodex", "AI Squad"))
                        GlobalState.characters = listOf("lopescodex", "AI Squad")
                    }
                } ?: run {
                    callback(listOf("lopescodex", "AI Squad"))
                    GlobalState.characters = listOf("lopescodex", "AI Squad")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("GuildWars2Api", "API call failed: ${e.message}")
                callback(listOf("lopescodex", "AI Squad"))
                GlobalState.characters = listOf("lopescodex", "AI Squad")
            }
        })
    }



    fun fetchGuildInfo(callback: (GuildInfo?) -> Unit) {
        val request = Request.Builder()
            .url("https://api.guildwars2.com/v1/guild_details?guild_name=Battlecross") // Hard coded fetch request
            .build()

        // API request for guild information
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonResponse ->
                    try {
                        if (jsonResponse.isNotEmpty()) {
                            val guildInfo: GuildInfo = Json.decodeFromString<GuildInfo>(jsonResponse)
                            callback(guildInfo)
                            GlobalState.guildInfo = guildInfo
                            GlobalState.emblem = guildInfo.emblem
                        } else {
                            callback(getFallbackGuildInfo())
                        }
                    } catch (e: IOException) {
                        Log.e("GuildWars2Api", "Invalid JSON format: ${e.message}")
                        callback(getFallbackGuildInfo())
                    }
                } ?: run {
                    callback(getFallbackGuildInfo())
                }
            }
            // If request fails
            override fun onFailure(call: Call, e: IOException) {
                Log.e("GuildWars2Api", "API call failed: ${e.message}")
                callback(getFallbackGuildInfo())
            }
        })
    }

    // Fetches either the background or foreground data for the Emblem.
    suspend fun fetchEmblemLayers(id: Int, type: String, ): List<String> {

        // Allows coroutine to be cancelled properly
        return suspendCancellableCoroutine { continuation ->
            val url = "https://api.guildwars2.com/v2/emblem/$type?ids=$id" // Initializes URL using the function parameters
            val request = Request.Builder().url(url).build() // builds the Request object.

            // Client creates a new call
            client.newCall(request).enqueue(object : Callback {
               // If the call fails it will use fallback data.
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) {
                        val fallback : List<String> = GuildWars2Api().getFallbackEmblemLayer(type).layers
                        continuation.resumeWith(Result.success(fallback))
                    }

                }
                // This call will return a single element array of a JSON object
                override fun onResponse(call: Call, response: Response) {

                    // Tries to parse through JSON object
                    // Returns the fallback data if it catches an error.
                    try {
                        val jsonResponse = response.body?.string()
                        if(jsonResponse?.isNotEmpty() == true) {
                            val emblemLayer = Json.decodeFromString<List<EmblemLayer>>(jsonResponse)
                            val layers = emblemLayer.firstOrNull()?.layers ?: emptyList()
                            GlobalState.emblemLayer = emblemLayer.firstOrNull()
                            continuation.resumeWith(Result.success(layers))

                        } else {
                            continuation.resumeWith(Result.success(GuildWars2Api().getFallbackEmblemLayer(type).layers))
                        }
                    } catch (e: Exception) {
                        if(continuation.isActive) {
                            continuation.resumeWithException(e)
                        }
                    }
                }
            })
            continuation.invokeOnCancellation {
                continuation.cancel()
            }

        }

    }


    suspend fun fetchItemIds(): List<Int> {
        return suspendCancellableCoroutine { continuation ->
            val url = "https://api.guildwars2.com/v2/items"
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) {
                        continuation.resumeWith(Result.success(emptyList()))
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val jsonResponse = response.body?.string()
                        if(jsonResponse?.isNotEmpty() == true) {
                            val itemIds = Json.decodeFromString<List<Int>>(jsonResponse)
                            continuation.resumeWith(Result.success(itemIds))
                            GlobalState.itemIDs = itemIds
                        } else {
                            continuation.resumeWith(Result.success(emptyList()))
                        }
                    } catch (e: Exception) {
                        if(continuation.isActive) {
                            continuation.resumeWithException(e)
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                continuation.cancel()
            }
        }
    }


    suspend fun fetchItemDetails(id: Int): ItemDetail? {
        val json = Json {
            ignoreUnknownKeys = true
        }
        return suspendCancellableCoroutine { continuation ->
            val url = "https://api.guildwars2.com/v2/items?ids=$id"
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) {
                        continuation.resumeWith(Result.success(null))
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val jsonResponse = response.body?.string()
                        if(jsonResponse?.isNotEmpty() == true) {

                            var consumable: Consumable? = null
                            // Reads through JSON to determine item type.
                            val itemType = json.decodeFromString<List<ItemType>>(jsonResponse)
                            if(itemType.isNotEmpty() && itemType.get(0).type == "Consumable") {

                                consumable = json.decodeFromString<List<Consumable>>(jsonResponse)[0]
                            }
                            val itemDetail = json.decodeFromString<List<ItemDetail>>(jsonResponse)
                            continuation.resumeWith(Result.success(itemDetail.firstOrNull()))
                        } else {
                            continuation.resumeWith(Result.success(null))
                        }
                    } catch (e: Exception) {
                        if(continuation.isActive) {
                            Log.e("GuildWars2Api", "Unexpected error: ${e.message}")
                            continuation.resumeWithException(e)
                        }
                    }
                }
            })

            continuation.invokeOnCancellation {
                continuation.cancel()
            }
        }
    }

}

