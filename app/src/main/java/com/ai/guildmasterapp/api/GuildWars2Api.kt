package com.ai.guildmasterapp.api

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
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

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("GuildWars2Api", "API call failed: ${e.message}")
                callback(getFallbackCharacterDetails())
            }
        })
    }

    fun fetchPvpDetails(apiKey: String, callback: (PvPStats?) -> Unit) {
        val request = Request.Builder()
            .url("https://api.guildwars2.com/v2/pvp/stats?access_token=$apiKey")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let { jsonResponse ->
                    try {
                        if (jsonResponse.isNotEmpty()) {
                            val pvpStats = Json.decodeFromString<PvPStats>(jsonResponse)
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

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("GuildWars2Api", "API call failed: ${e.message}")
                callback(listOf("lopescodex", "AI Squad"))
                GlobalState.characters = listOf("lopescodex", "AI Squad")
            }
        })
    }
}

