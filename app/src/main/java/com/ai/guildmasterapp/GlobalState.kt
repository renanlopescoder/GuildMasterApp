package com.ai.guildmasterapp

import kotlinx.serialization.Serializable

object GlobalState {
    var characters: List<String>? = null
    var characterDetail: CharacterDetail? = null
    var pvpStats: PvPStats? = null
}

// ====================== ============================ ====================== //
// ====================== Character Details Data Model ====================== //
// ====================== ============================ ====================== //

@Serializable
data class CharacterDetail(
    val name: String,
    val race: String,
    val gender: String,
    val flags: List<String?>,
    val profession: String,
    val guild: String?,
    val skills: Skills?,
    val level: Int,
    val age: Int,
    val created: String,
    val deaths: Int,
    val equipment: List<Equipment>,
    val bags: List<Bag?>,
    val backstory: List<String?>,
    val wvw_abilities: List<String?>,
    val recipes: List<String?>,
    val training: List<String?>,
    val specializations: Specializations,
    val crafting: List<String?>,
    val equipment_pvp: EquipmentPvp?
)

@Serializable
data class EquipmentPvp(
    val amulet: String?,
    val rune: String?,
    val sigils: List<String?>
)

@Serializable
data class Skills(
    val pve: SkillDetails?,
    val pvp: SkillDetails?,
    val wvw: SkillDetails?,
)

@Serializable
data class SkillDetails(
    val heal: Int?,
    val utilities: List<String?>? = null,
    val elite: String?,
    val legends: List<String?>? = null
)

@Serializable
data class Specializations(
    val pve: List<SpecializationDetails>,
    val pvp: List<SpecializationDetails>,
    val wvw: List<SpecializationDetails>,
)

@Serializable
data class SpecializationDetails(
    val id: String?,
    val traits: List<String?>
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

// ====================== ==================== ====================== //
// ====================== PVP Stats Data Model ====================== //
// ====================== ==================== ====================== //

@Serializable
data class PvPStats(
    val pvp_rank: Int,
    val pvp_rank_points: Int,
    val pvp_rank_rollovers: Int,
    val aggregate: Aggregate,
    val professions: Professions,
    val ladders: Ladders
)

@Serializable
data class Aggregate(
    val wins: Int,
    val losses: Int,
    val desertions: Int,
    val byes: Int,
    val forfeits: Int
)

@Serializable
data class Professions(
    val elementalist: ProfessionStats,
    val guardian: ProfessionStats,
    val mesmer: ProfessionStats,
    val necromancer: ProfessionStats,
    val warrior: ProfessionStats,
    val ranger: ProfessionStats,
    val thief: ProfessionStats,
    val engineer: ProfessionStats,
    val revenant: ProfessionStats
)

@Serializable
data class ProfessionStats(
    val wins: Int,
    val losses: Int,
    val desertions: Int,
    val byes: Int,
    val forfeits: Int
)

@Serializable
data class Ladders(
    val ranked: LadderStats,
    val unranked: LadderStats
)

@Serializable
data class LadderStats(
    val wins: Int,
    val losses: Int,
    val desertions: Int,
    val byes: Int,
    val forfeits: Int
)
