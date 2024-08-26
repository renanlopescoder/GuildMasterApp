package com.ai.guildmasterapp

import kotlinx.serialization.Serializable

object GlobalState {
    var characters: List<String>? = null
    var characterDetail: CharacterDetail? = null
    var guildInfo: GuildInfo? = null
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


// Guild details data class
@Serializable
data class GuildInfo(
    val id: String,
    val name: String,
    val tag: String,
    val emblem: Emblem,
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