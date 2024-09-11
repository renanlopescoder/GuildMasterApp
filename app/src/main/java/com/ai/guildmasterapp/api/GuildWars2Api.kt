package com.ai.guildmasterapp.api

import android.provider.Settings.Global
import android.util.Log
import com.ai.guildmasterapp.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import okhttp3.Call

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

        val json = Json{ignoreUnknownKeys = true }

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: Call, response: okhttp3.Response) {
                response.body?.string()?.let { jsonResponse ->
<<<<<<< Updated upstream

=======
                    Log.d("JSON RESPONSE", jsonResponse)
>>>>>>> Stashed changes
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
            professions = mapOf(
                "elementalist" to ProfessionStats(35, 25, 1, 0, 0),
                "guardian" to ProfessionStats(20, 15, 0, 1, 0),
                "mesmer" to ProfessionStats(18, 22, 1, 1, 0),
                "necromancer" to ProfessionStats(10, 8, 0, 1, 0),
                "warrior" to ProfessionStats(15, 10, 0, 0, 0),
                "ranger" to ProfessionStats(12, 14, 1, 2, 1),
                "thief" to ProfessionStats(8, 12, 0, 0, 0),
                "engineer" to ProfessionStats(5, 9, 0, 0, 0),
                "revenant" to ProfessionStats(5, 7, 0, 0, 0)
            ),


//            Professions(
//                elementalist = ProfessionStats(wins = 35, losses = 25, desertions = 1, byes = 0, forfeits = 0),
//                guardian = ProfessionStats(wins = 20, losses = 15, desertions = 0, byes = 1, forfeits = 0),
//                mesmer = ProfessionStats(wins = 18, losses = 22, desertions = 1, byes = 1, forfeits = 0),
//                necromancer = ProfessionStats(wins = 10, losses = 8, desertions = 0, byes = 1, forfeits = 0),
//                warrior = ProfessionStats(wins = 15, losses = 10, desertions = 0, byes = 0, forfeits = 0),
//                ranger = ProfessionStats(wins = 12, losses = 14, desertions = 1, byes = 2, forfeits = 1),
//                thief = ProfessionStats(wins = 8, losses = 12, desertions = 0, byes = 0, forfeits = 0),
//                engineer = ProfessionStats(wins = 5, losses = 9, desertions = 0, byes = 0, forfeits = 0),
//                revenant = ProfessionStats(wins = 5, losses = 7, desertions = 0, byes = 0, forfeits = 0)
//            ),
            ladders = Ladders(
                ranked = LadderStats(wins = 78, losses = 67, desertions = 2, byes = 3, forfeits = 1),
                unranked = LadderStats(wins = 50, losses = 35, desertions = 1, byes = 2, forfeits = 0)
            )
        )

        GlobalState.pvpStats = fallbackPvPStats
        return fallbackPvPStats
    }




    private fun fetchCharacters(apiKey: String, callback: (List<String>?) -> Unit) {
        val request = Request.Builder()
            .url("https://api.guildwars2.com/v2/characters?access_token=$apiKey")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: Call, response: okhttp3.Response) {
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

}


