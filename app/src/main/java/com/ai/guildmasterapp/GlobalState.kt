package com.ai.guildmasterapp

import kotlinx.serialization.Serializable


object GlobalState {
    var characters: List<String>? = null
    var characterDetail: CharacterDetail? = null
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
<<<<<<< Updated upstream
)
=======
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
    val professions: Map<String,ProfessionStats>,
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
    val wins: Int = 0,
    val losses: Int = 0,
    val desertions: Int = 0,
    val byes: Int = 0,
    val forfeits: Int = 0
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
>>>>>>> Stashed changes
