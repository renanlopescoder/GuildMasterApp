package com.ai.guildmasterapp.api

import android.provider.Settings.Global
import android.util.Log
import com.ai.guildmasterapp.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.CharacterDetail
import com.ai.guildmasterapp.Equipment
import com.ai.guildmasterapp.Item
import com.ai.guildmasterapp.Bag
import com.ai.guildmasterapp.Specializations
import com.ai.guildmasterapp.SpecializationDetails
import com.ai.guildmasterapp.Skills
import com.ai.guildmasterapp.SkillDetails
import com.ai.guildmasterapp.EquipmentPvp

import com.ai.guildmasterapp.PvPStats
import com.ai.guildmasterapp.Aggregate
import com.ai.guildmasterapp.Professions
import com.ai.guildmasterapp.ProfessionStats
import com.ai.guildmasterapp.Ladders
import com.ai.guildmasterapp.LadderStats

import kotlin.coroutines.resumeWithException
import okhttp3.Call
import okhttp3.*

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

    fun getPvpStats(callback: (PvPStats?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            fetchApiKey(uid) { apiKey ->
                if (apiKey != null) {
                    fetchPvpDetails(apiKey, callback)
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

        val json = Json{ignoreUnknownKeys = true }

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonResponse ->

                    try {
                        if (jsonResponse.isNotEmpty()) {
                            val characterDetails = json.decodeFromString<List<CharacterDetail>>(jsonResponse)[0]
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

    fun fetchPvpDetails(apiKey: String, callback: (PvPStats?) -> Unit) {
        val request = Request.Builder()
            .url("https://api.guildwars2.com/v2/pvp/stats?access_token=$apiKey")
            .build()

        val json = Json{ignoreUnknownKeys = true }

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let { jsonResponse ->
                    try {
                        if (jsonResponse.isNotEmpty()) {
                            val pvpStats = json.decodeFromString<PvPStats>(jsonResponse)
                            callback(pvpStats)
                            GlobalState.pvpStats = pvpStats
                        } else {
                            callback(getFallbackPvPStats())
                        }
                    } catch (e: IOException) {
                        Log.e("GuildWars2Api", "Invalid JSON format: ${e.message}")
                        callback(getFallbackPvPStats())
                    } catch (e: Exception) {
                        Log.e("GuildWars2Api", "Unexpected error: ${e.message}")
                        callback(getFallbackPvPStats())
                    }
                } ?: run {
                    callback(getFallbackPvPStats())
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("GuildWars2Api", "API call failed: ${e.message}")
                callback(getFallbackPvPStats())
            }
        })
    }


    private fun getFallbackCharacterDetails(): CharacterDetail {
        val char = CharacterDetail(
            name = "Ai Senpai Squad",
            race = "Asura",
            gender = "Male",
            flags = emptyList(),
            guild = null,
            profession = "Revenant",
            level = 4,
            age = 1880,
            created = "2024-08-12T12:03:00Z",
            deaths = 0,
            equipment = listOf(
                Equipment(id = 6479, slot = "HelmAquatic", binding = "Character", bound_to = "Ai Senpai Squad"),
                Equipment(id = 6459, slot = "Coat", binding = "Character", bound_to = "Ai Senpai Squad", dyes = listOf(45, 45, 376, null)),
                Equipment(id = 6458, slot = "Boots", binding = "Character", bound_to = "Ai Senpai Squad", dyes = listOf(377, 19, 377, null)),
                Equipment(id = 69244, slot = "Helm", binding = "Character", bound_to = "Ai Senpai Squad", dyes = listOf(45, 376, 377, null)),
                Equipment(id = 6461, slot = "Leggings", binding = "Character", bound_to = "Ai Senpai Squad", dyes = listOf(45, 45, 376, null)),
                Equipment(id = 33360, slot = "WeaponAquaticA"),
                Equipment(id = 30830, slot = "WeaponA1"),
                Equipment(id = 97730, slot = "FishingRod", binding = "Account")
            ),
            bags = listOf(
                Bag(id = 8932, size = 20, inventory = listOf(
                    Item(id = 33349, count = 1),
                    Item(id = 32135, count = 1, binding = "Character", bound_to = "Ai Senpai Squad"),
                    Item(id = 98599, count = 1, binding = "Character", bound_to = "Ai Senpai Squad"),
                    Item(id = 92876, count = 1, binding = "Account"),
                    Item(id = 92941, count = 1, binding = "Account"),
                    Item(id = 24272, count = 2),
                    Item(id = 12143, count = 1),
                    null, null, null, null, null, null, null, null, null, null, null, null, null
                )),
                null, null, null, null
            ),
            backstory = listOf("7-54", "10-69", "12-73", "11-70", "189-187"),
            wvw_abilities = emptyList(),
            recipes = emptyList(),
            training = emptyList(),
            specializations = Specializations(
                pve = listOf(
                    SpecializationDetails(id = null, traits = listOf(null, null, null)),
                    SpecializationDetails(id = null, traits = listOf(null, null, null)),
                    SpecializationDetails(id = null, traits = listOf(null, null, null))
                ),
                pvp = listOf(
                    SpecializationDetails(id = null, traits = listOf(null, null, null)),
                    SpecializationDetails(id = null, traits = listOf(null, null, null)),
                    SpecializationDetails(id = null, traits = listOf(null, null, null))
                ),
                wvw = listOf(
                    SpecializationDetails(id = null, traits = listOf(null, null, null)),
                    SpecializationDetails(id = null, traits = listOf(null, null, null)),
                    SpecializationDetails(id = null, traits = listOf(null, null, null))
                )
            ),
            crafting = listOf(),
            equipment_pvp = EquipmentPvp(
                amulet = null,
                rune = null,
                sigils = listOf(null, null, null, null)
            ),
            skills = Skills(
                pve = SkillDetails(
                    heal = 62719,
                    utilities = listOf(null, null, null),
                    elite = null,
                    legends = listOf("Air")
                ),
                pvp = SkillDetails(
                    heal = 62719,
                    utilities = listOf(null, null, null),
                    elite = null,
                    legends = listOf("Air")
                ),
                wvw = SkillDetails(
                    heal = 62719,
                    utilities = listOf(null, null, null),
                    elite = null,
                    legends = listOf("Air")
                )
            )
        )

        GlobalState.characterDetail = char
        return char
    }


    private fun getFallbackGuildInfo(): GuildInfo {

        val char = GuildInfo(
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

    // Mock data for Guild members recycler list
    fun getFallbackGuildMembers(): MutableList<GuildMember> {

        val members = mutableListOf<GuildMember>(
            GuildMember("Renan", "Leader", "2024-07-22"),
            GuildMember("Jesse", "Officer", "2024-07-29"),
            GuildMember("Kennan", "Member", "2024-08-15"),
            GuildMember("Jane", "Member", "2024-08-29"),
            GuildMember("John", "Officer", "2024-07-17"),
            GuildMember("Mike", "Member", "2024-08-05"),
        )

        GlobalState.guildMembers = members
        return members
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

    private fun getFallbackPvPStats(): PvPStats {
        val fallbackPvPStats = PvPStats(
            pvp_rank = 23,
            pvp_rank_points = 18952,
            pvp_rank_rollovers = 2,
            aggregate = Aggregate(
                wins = 128,
                losses = 102,
                desertions = 3,
                byes = 5,
                forfeits = 1
            ),
            professions = Professions(
                elementalist = ProfessionStats(wins = 35, losses = 25, desertions = 1, byes = 0, forfeits = 0),
                guardian = ProfessionStats(wins = 20, losses = 15, desertions = 0, byes = 1, forfeits = 0),
                mesmer = ProfessionStats(wins = 18, losses = 22, desertions = 1, byes = 1, forfeits = 0),
                necromancer = ProfessionStats(wins = 10, losses = 8, desertions = 0, byes = 1, forfeits = 0),
                warrior = ProfessionStats(wins = 15, losses = 10, desertions = 0, byes = 0, forfeits = 0),
                ranger = ProfessionStats(wins = 12, losses = 14, desertions = 1, byes = 2, forfeits = 1),
                thief = ProfessionStats(wins = 8, losses = 12, desertions = 0, byes = 0, forfeits = 0),
                engineer = ProfessionStats(wins = 5, losses = 9, desertions = 0, byes = 0, forfeits = 0),
                revenant = ProfessionStats(wins = 5, losses = 7, desertions = 0, byes = 0, forfeits = 0)
            ),
            ladders = Ladders(
                ranked = LadderStats(wins = 78, losses = 67, desertions = 2, byes = 3, forfeits = 1),
                unranked = LadderStats(wins = 50, losses = 35, desertions = 1, byes = 2, forfeits = 0)
            )
        )

        GlobalState.pvpStats = fallbackPvPStats
        return fallbackPvPStats
    }


    private fun getFallbackBackstoryAnswer(): BackstoryAnswers {
        val fallbackBackstoryAnswer = BackstoryAnswers(
            id = "",
            title = "Answer",
            description = "Failed to retrieve answer.",
            journal = "",
            question = -1
        )

        return fallbackBackstoryAnswer
    }


    private fun getFallbackBackstoryQuestion(): BackstoryQuestion {
        val fallbackBackstoryQuestion = BackstoryQuestion(
            id = -1,
            title = "Question",
            description = "Failed to retrieve question."
        )

        return fallbackBackstoryQuestion
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
    suspend fun fetchEmblemLayers(id: Int, type: String ): List<String> {

        // Allows coroutine to be cancelled properly
        return suspendCancellableCoroutine { continuation ->
            val url =
                "https://api.guildwars2.com/v2/emblem/$type?ids=$id" // Initializes URL using the function parameters
            val request = Request.Builder().url(url).build() // builds the Request object.

            // Client creates a new call
            client.newCall(request).enqueue(object : Callback {
                // If the call fails it will use fallback data.
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) {
                        val fallback: List<String> = GuildWars2Api().getFallbackEmblemLayer(type).layers
                        continuation.resumeWith(Result.success(fallback))
                    }

                }

                // This call will return a single element array of a JSON object
                override fun onResponse(call: Call, response: Response) {

                    // Tries to parse through JSON object
                    // Returns the fallback data if it catches an error.
                    try {
                        val jsonResponse = response.body?.string()
                        if (jsonResponse?.isNotEmpty() == true) {
                            val emblemLayer = Json.decodeFromString<List<EmblemLayer>>(jsonResponse)
                            val layers = emblemLayer.firstOrNull()?.layers ?: emptyList()

                            continuation.resumeWith(Result.success(layers))

                        } else {
                            continuation.resumeWith(Result.success(GuildWars2Api().getFallbackEmblemLayer(type).layers))
                        }
                    } catch (e: Exception) {
                        if (continuation.isActive) {
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

    // Fetches a list of almost every item id in the game
    suspend fun fetchItemIds(){
        return suspendCancellableCoroutine { continuation ->
            val url = "https://api.guildwars2.com/v2/items"
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) {
                        continuation.resumeWith(Result.success(Unit))
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val jsonResponse = response.body?.string()
                        if (jsonResponse?.isNotEmpty() == true) {
                            val itemIds = Json.decodeFromString<List<Int>>(jsonResponse)
                            continuation.resumeWith(Result.success(Unit))
                            GlobalState.itemIDs = itemIds
                        } else {
                            continuation.resumeWith(Result.success(Unit))
                        }
                    } catch (e: Exception) {
                        if (continuation.isActive) {
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

    // fetches item detail, determines it's type and then stores it in the respective global state map
    // Returns ItemType data class
    suspend fun fetchItemDetails(id: Int): ItemType? {

        var result: ItemType? = null

        val customJson = Json {
            ignoreUnknownKeys = true
        }

        val url = "https://api.guildwars2.com/v2/items?ids=$id"

        return suspendCancellableCoroutine { continuation ->

            val request = Request.Builder()
                .url(url)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(e)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val jsonResponse = response.body?.string()

                        if (jsonResponse?.isNotEmpty() == true) {

                            var weapon: Weapon? = null
                            var consumable: Consumable? = null
                            var backItem: BackItem? = null
                            var armor: Armor? = null

                            // Reads through JSON to determine item type.
                            val itemType = customJson.decodeFromString<List<ItemType>>(jsonResponse)
                            if (itemType.isNotEmpty()) {

                                result = itemType[0]
                                when (itemType[0].type) {
                                    "Weapon" -> {
                                        weapon = customJson.decodeFromString<List<Weapon>>(jsonResponse)[0]
                                        GlobalState.weaponMap[id] = weapon
                                    }

                                    "Consumable" -> {
                                        consumable = customJson.decodeFromString<List<Consumable>>(jsonResponse)[0]
                                        GlobalState.consumableMap[id] = consumable
                                    }

                                    "Back" -> {
                                        backItem = customJson.decodeFromString<List<BackItem>>(jsonResponse)[0]
                                        GlobalState.backItemMap[id] = backItem
                                    }

                                    "Armor" -> {
                                        armor = customJson.decodeFromString<List<Armor>>(jsonResponse)[0]
                                        GlobalState.armorMap[id] = armor
                                    }
                                }
                            }

                            continuation.resumeWith(Result.success(result))

                        }
                    } catch (e: Exception) {
                        if (continuation.isActive) {
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



    suspend fun fetchBackstoryAnswer(id: String): BackstoryAnswers? {
        var result: BackstoryAnswers? = null

        val customJson = Json {
            ignoreUnknownKeys = true
        }

        return suspendCancellableCoroutine { continuation ->

            val request = Request.Builder()
                .url("https://api.guildwars2.com/v2/backstory/answers/$id")
                .build()

            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(e)
                    }
                }


                override fun onResponse(call: Call, response: Response) {

                    try {
                        val jsonResponse = response.body?.string()

                        if (jsonResponse?.isNotEmpty() == true) {
                            val backstoryAnswers = customJson.decodeFromString<BackstoryAnswers>(jsonResponse)
                            result = backstoryAnswers
                        } else {
                            result = getFallbackBackstoryAnswer()
                        }

                        continuation.resumeWith(Result.success(result))

                    } catch (e: Exception) {
                        if (continuation.isActive) {
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



    suspend fun fetchBackstoryQuestions(id: Int): BackstoryQuestion? {
        var result: BackstoryQuestion? = null

        val customJson = Json {
            ignoreUnknownKeys = true
        }

        return suspendCancellableCoroutine { continuation ->

            val request = Request.Builder()
                .url("https://api.guildwars2.com/v2/backstory/questions/$id")
                .build()

            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(e)
                    }
                }


                override fun onResponse(call: Call, response: Response) {

                    try {
                        val jsonResponse = response.body?.string()

                        if (jsonResponse?.isNotEmpty() == true) {
                            val backstoryQuestion = customJson.decodeFromString<BackstoryQuestion>(jsonResponse)
                            result = backstoryQuestion
                        } else {
                            result = getFallbackBackstoryQuestion()
                        }

                        continuation.resumeWith(Result.success(result))

                    } catch (e: Exception) {
                        if (continuation.isActive) {
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




