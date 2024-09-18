package com.ai.guildmasterapp

import android.media.metrics.Event
import kotlinx.serialization.Serializable


object GlobalState {
    var characters: List<String>? = null
    var characterDetail: CharacterDetail? = null
    var pvpStats: PvPStats? = null
    var guildInfo: GuildInfo? = null
    var guildMembers: List<GuildMember>? = null
    var itemIDs: List<Int>? = null

    // Stores respective item from the game & their details using the item ID as the key.
    var weaponMap: MutableMap<Int, Weapon> = mutableMapOf()
    var armorMap: MutableMap<Int, Armor> = mutableMapOf()
    var consumableMap: MutableMap<Int, Consumable> = mutableMapOf()
    var backItemMap: MutableMap<Int, BackItem> = mutableMapOf()
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
    val dyes: List<Int?>? = null,
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


// Guild details data class
@Serializable
data class GuildInfo(
    val guild_id: String,
    val guild_name: String,
    val tag: String,
    val emblem: Emblem,
)

// Data class for Guild members
@Serializable
data class GuildMember(
    val name: String,
    val rank: String,
    val joined: String
)

// Stores information for guild emblem.
@Serializable
data class Emblem(
    val background_id: Int,
    val foreground_id: Int,
    val flags: List<String>,
    val background_color_id: Int,
    val foreground_primary_color_id: Int,
    val foreground_secondary_color_id: Int
)

// Stores the URLs to make up the background or foreground images of the Emblem
@Serializable
data class EmblemLayer(
    val id: Int,
    val layers: List<String>
)



// Data class for every item
// Stores item name & type
@Serializable
data class ItemType(
    val name: String,
    val type: String,
    val id: Int
)

// Weapons related data classes
@Serializable
data class Weapon(
    val name: String,
    val description: String = "",
    val type: String,
    val rarity: String,
    val vendor_value: Int,
    val default_skin: Int,
    val game_types: List<String>,
    val flags: List<String>,
    val restrictions: List<String>,
    val id: Int,
    val chat_link: String,
    val icon: String,
    val details: WeaponDetails,

)

// Details for weapons
@Serializable
data class WeaponDetails(
    val type: String,
    val damage_type: String,
    val min_power: Int,
    val max_power: Int,
    val defense: Int,
    val infusion_slots: List<String>,
    val attribute_adjustment: Double,
)



// Consumable related data classes
@Serializable
data class Consumable(
    val name: String,
    val description: String = "",
    val type: String,
    val level: Int,
    val rarity: String,
    val vendor_value: Int,
    val default_skin: Int = 0,
    val game_types: List<String>,
    val flags: List<String>,
    val restrictions: List<String>,
    val id: Int,
    val chat_link: String,
    val icon: String,
    val details: ConsumableDetails,
)


// Details for Consumable item
@Serializable
data class ConsumableDetails(
    val type: String,
)

// Back item related data classes
@Serializable
data class BackItem(
    val name: String,
    val description: String = "",
    val type: String,
    val level: Int,
    val rarity: String,
    val vendor_value: Int,
    val default_skin: Int,
    val game_types: List<String>,
    val flags: List<String>,
    val restrictions: List<String>,
    val id: Int,
    val chat_link: String,
    val icon: String,
    val  details: BackItemDetails
)

// Details for Back Item
@Serializable
data class BackItemDetails(
    val infusion_slots: List<String>? = null,
    val attribute_adjustment: Double,
    val infix_upgrade: InfixUpgrade? = null,
)



// Armor related data classes
@Serializable
data class Armor(
    val name: String,
    val description: String = "",
    val type: String,
    val level: Int,
    val rarity: String,
    val vendor_value: Int,
    val default_skin: Int,
    val game_types: List<String>,
    val flags: List<String>,
    val restrictions: List<String>,
    val id: Int,
    val chat_link: String,
    val icon: String,
    val details: ArmorDetails
)

// Details for Armors
@Serializable
data class ArmorDetails(
    val type: String,
    val weight_class: String,
    val defense: Int,
    val infusion_slots: List<String>? = null,
    val attribute_adjustment: Double,
    val infix_upgrade: InfixUpgrade? = null,
    val suffix_item_id: Int? = null,
    val secondary_suffix_item_id: String? = null,
)

// Object for InfixUpgrade parameter
@Serializable
data class InfixUpgrade(
    val id: Int,
    val attributes: List<Attribute>
)
// Object for attribute parameter
@Serializable
data class Attribute(
    val attribute: String,
    val modifier: Int
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


@Serializable
data class EventJson(
    val events: Map<String, EventDetails>
)


@Serializable
data class EventDetails(
    val name: String,
    val level: Int,
    val map_id: Int,
    val flags: List<String>,
)


@Serializable
data class MapDetails(
    val id: Int,
    val name: String,
    val min_level: Int,
    val max_level: Int,
    val default_floor: Int,
    val type: String
)