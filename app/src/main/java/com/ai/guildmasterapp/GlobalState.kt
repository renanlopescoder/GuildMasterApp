package com.ai.guildmasterapp

import kotlinx.serialization.Serializable

object GlobalState {
    var characters: List<String>? = null
    var characterDetail: CharacterDetail? = null
    var equipmentStats: List<EquipmentStats>? = null
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
data class EquipmentStats(
    val id: Int,
    val name: String,
    val attributes: List<Attribute?>? = null,
)

@Serializable
data class Attribute(
    val name: String,
    val value: String
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