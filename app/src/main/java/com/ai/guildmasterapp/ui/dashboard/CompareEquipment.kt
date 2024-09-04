@file:Suppress("UNUSED_EXPRESSION")

package com.ai.guildmasterapp.ui.dashboard

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.R
import com.squareup.picasso.Picasso

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

//Delete After Testing
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.platform.android.AndroidLogHandler.setLevel
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


@Serializable
data class Item(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val type: String = "",
    val level: Int = 0,
    val rarity: String = "",
    val vendor_value: Int = 0,
    val default_skin: Int = 0,
    val game_types: List<String>,
    val flags: List<String>,
    val restrictions: List<String>,
    val chat_link: String = "",
    val icon: String = "",
    val details: ItemDetails
)
@Serializable
data class ItemDetails(
    val type: String = "",
    val damage_type: String= "",
    val min_power: Int = 0,
    val max_power: Int = 0,
    val defense: Int = 0,
    val infusion_slots: List<String>,
    val attribute_adjustment: Double = 0.0,
    val infix_upgrade: InfixUpgrade?,
    val suffix_item_id: Int? = 0,
    val secondary_suffix_item_id: String?
)
@Serializable
data class InfixUpgrade(
    val id: Int,
    val attributes: List<Attribute>
)
@Serializable
data class Attribute(
    val attribute: String,
    val modifier: Int
)

@Serializable
data class CharacterAttributes(
    var defense: Int = 0,
    var power: Int = 0,
    var toughness: Int = 0,
    var precision: Int = 0,
    var vitality: Int = 0,
    var concentration: Int = 0,
    var conditionDamage: Int = 0,
    var expertise: Int = 0,
    var ferocity: Int = 0,
    var healingPower: Int = 0
)




class CompareEquipment : AppCompatActivity() {

    private lateinit var helmImage: ImageView
    private lateinit var shoulderImage: ImageView
    private lateinit var chestImage: ImageView
    private lateinit var handsImage: ImageView
    private lateinit var legsImage: ImageView
    private lateinit var feetImage: ImageView
    // Text Views
    private lateinit var defenseAttribute: TextView
    private lateinit var powerAttribute: TextView
    private lateinit var toughnessAttribute: TextView
    private lateinit var precisionAttribute: TextView
    private lateinit var vitalityAttribute: TextView
    private lateinit var concentrationAttribute: TextView
    private lateinit var conditionDamageAttribute: TextView
    private lateinit var expertiseAttribute: TextView
    private lateinit var ferocityAttribute: TextView
    private lateinit var healingPowerAttribute: TextView

    // DELETE AFTER TESTING
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30,TimeUnit.SECONDS)
        .writeTimeout(30,TimeUnit.SECONDS)
        .build()

    val itemList = mutableListOf<Item>()
    var baseAttributes = CharacterAttributes()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipment_builder)
        supportActionBar?.hide()

        //Back button, when pressed it will close the activity
        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener { returnToMain() }


        helmImage = findViewById(R.id.equipment_helm)
        shoulderImage = findViewById(R.id.equipment_shoulders)
        chestImage = findViewById(R.id.equipment_chest)
        handsImage = findViewById(R.id.equipment_hands)
        legsImage = findViewById(R.id.equipment_legs)
        feetImage = findViewById(R.id.equipment_feet)

        defenseAttribute = findViewById(R.id.defense)
        powerAttribute = findViewById(R.id.power_attr)
        toughnessAttribute = findViewById(R.id.toughness_attr)
        precisionAttribute = findViewById(R.id.precision_attr)
        vitalityAttribute = findViewById(R.id.vitality_attr)
        concentrationAttribute = findViewById(R.id.concentration_attr)
        conditionDamageAttribute = findViewById(R.id.condition_attr)
        expertiseAttribute = findViewById(R.id.expertise_attr)
        ferocityAttribute = findViewById(R.id.ferocity_attr)
        healingPowerAttribute = findViewById(R.id.healing_power_attr)


//        This is a list of strings that is used to sort any equipment the character has DO NOT CHANGE
        val armorList = listOf(
            "Helm",
            "Shoulders",
            "Coat",
            "Boots",
            "Gloves",
            "Leggings"
        )

//      Using Coroutines to divide the logic and the UI
        CoroutineScope(Dispatchers.IO).launch {
            // Loop through all the items in the characters equipment and if the item.slot matches a string from the armorList, fecth the items details and add to itemList
            for (item in GlobalState.characterDetail?.equipment!!) {
                if (item.slot in armorList) {
                    fetchAndAddItem(item.id)
                }
            }


//      Calculate the base attributes based on the characters level and store them in baseAttributes
            var baseAttributes = GlobalState?.characterDetail?.level?.let { calculateBaseAttributes(it) }


            var characterAttributes = CharacterAttributes()

            for (item in itemList) {
                when (item.details.type) {
                    "Helm" -> {
                        val helmAttributes: CharacterAttributes = calculateInfixUpgrades(item)
                        addAttributes(characterAttributes, helmAttributes)
                    }

                    "Shoulders" -> {
                        val shouldersAttributes: CharacterAttributes = calculateInfixUpgrades(item)
                        addAttributes(characterAttributes, shouldersAttributes)
                    }

                    "Coat" -> {
                        val coatAttributes: CharacterAttributes = calculateInfixUpgrades(item)
                        addAttributes(characterAttributes, coatAttributes)
                    }

                    "Boots" -> {
                        val bootsAttributes: CharacterAttributes = calculateInfixUpgrades(item)
                        addAttributes(characterAttributes, bootsAttributes)
                    }

                    "Gloves" -> {
                        val glovesAttributes: CharacterAttributes = calculateInfixUpgrades(item)
                        addAttributes(characterAttributes, glovesAttributes)
                    }

                    "Leggings" -> {
                        val legsAttributes: CharacterAttributes = calculateInfixUpgrades(item)
                        addAttributes(characterAttributes, legsAttributes)
                    }
                }
            }

            if (baseAttributes != null) {
                addAttributes(characterAttributes, baseAttributes)
            }

//      Calculate the defense by adding all the equipments defense values together, and add toughness from baseAttributes
            if (characterAttributes != null) {
                characterAttributes.defense = (calculateInitialDefense() + baseAttributes?.toughness!!)
            }

            runOnUiThread {

//              Loop through itemList and match the item to the Equipment slot, update image using Picasso
                itemList.forEach { item ->
                    when (item.details.type) {
                        "Helm" -> loadImageWithPicasso(helmImage, item.icon)
                        "Shoulders" -> loadImageWithPicasso(shoulderImage, item.icon)
                        "Coat" -> loadImageWithPicasso(chestImage, item.icon)
                        "Boots" -> loadImageWithPicasso(feetImage, item.icon)
                        "Gloves" -> loadImageWithPicasso(handsImage, item.icon)
                        "Leggings" -> loadImageWithPicasso(legsImage, item.icon)
                    }
                }

//              Set all attributes from the characterAttributes
                setDefense(characterAttributes.defense)
                setPower(characterAttributes.power)
                setToughness(characterAttributes.toughness)
                setPrecision(characterAttributes.precision)
                setVitality(characterAttributes.vitality)
                setConcentration(characterAttributes.concentration)
                setConditionDamage(characterAttributes.conditionDamage)
                setExpertise(characterAttributes.expertise)
                setFerocity(characterAttributes.ferocity)
                setHealingPower(characterAttributes.healingPower)

            }

        }


    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun returnToMain() {
        finish()
    }

    private fun calculateInitialDefense(): Int{
        var totalEquipmentDefense: Int = 0

        for (item in itemList) {
            totalEquipmentDefense += item.details.defense
        }

        return totalEquipmentDefense
    }

    private fun setDefense(newDefense: Int){
        val defenseString = getString(R.string.defense_attr) + newDefense.toString()
        defenseAttribute.text = defenseString
    }

    private fun setPower(newPower: Int){
        val powerString = getString(R.string.power_attribute) + newPower.toString()
        powerAttribute.text = powerString
    }

    private fun setToughness(newToughness: Int){
        val toString = getString(R.string.toughness_attribute) + newToughness.toString()
        toughnessAttribute.text = toString
    }

    private fun setPrecision(newPrecision: Int){
        val precisionString = getString(R.string.precision_attribute) + newPrecision.toString()
        precisionAttribute.text = precisionString
    }

    private fun setVitality(newVitality: Int){
        val vitalityString = getString(R.string.vitality_attribute) + newVitality.toString()
        vitalityAttribute.text = vitalityString
    }

    private fun setConcentration(newConcentration: Int){
        val concentrationString = getString(R.string.concentration_attribute) + newConcentration.toString()
        concentrationAttribute.text = concentrationString
    }

    private fun setConditionDamage(newConditionDamage: Int){
        val conditionString = getString(R.string.condition_attribute) + newConditionDamage.toString()
        conditionDamageAttribute.text = conditionString
    }

    private fun setExpertise(newExpertise: Int){
        val expertiseString = getString(R.string.expertise_attribute) + newExpertise.toString()
        expertiseAttribute.text = expertiseString
    }

    private fun setFerocity(newFerocity: Int){
        val ferocityString = getString(R.string.ferocity_attribute) + newFerocity.toString()
        ferocityAttribute.text = ferocityString
    }

    private fun setHealingPower(newHealingPower: Int){
        val healingPowerString = getString(R.string.healing_power_attribute) + newHealingPower.toString()
        healingPowerAttribute.text = healingPowerString
    }

    suspend fun fetchAndAddItem(itemId: Int) {
        val maxRetries = 3
        var currentAttempt = 0

        val json = Json{ignoreUnknownKeys = true}
        while (currentAttempt < maxRetries) {
            try {
                val request = Request.Builder()
                    .url("https://api.guildwars2.com/v2/items/$itemId")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        println("Failed to fetch item with id $itemId: ${response.message}")
                        return@use
                    }

                    response.body?.string()?.let { jsonString ->
                        try {
                            val item = json.decodeFromString<Item>(jsonString)
                            itemList.add(item)
                        } catch (e: Exception) {
                            println("Failed to parse item JSON: ${e.message}")
                        }
                    } ?: println("Empty response body for item with id $itemId")
                }

                break // Exit loop if successful
            } catch (e: SocketTimeoutException) {
                currentAttempt++
                if (currentAttempt >= maxRetries) {
                    println("Failed to fetch item after $maxRetries attempts")
                    throw e
                }
            }
        }
    }

    private fun loadImageWithPicasso(imageView: ImageView, url: String) {
        Log.d("Picasso", "$url")
        Picasso.get().load(url).into(imageView)
    }

    private fun calculateBaseAttributes(level: Int): CharacterAttributes{

        val baseAttributes = CharacterAttributes()

        //value that will be returned, starts at 37
        var totalValue = 37

        //value that will store the scalar as it iterates through the scalar values based on level.
        var scalar: Int

        //loop through the level and stop at the characters current level.
        for (lvl in 2..level) {
            scalar = when (lvl) {
                in 2..10 -> 7
                in 11..20 -> 10
                in 21..24 -> 14
                25, 26 -> 15
                in 27..30 -> 16
                in 31..40 -> 20
                in 41..44 -> 24
                45, 46 -> 25
                in 47..50 -> 26
                in 51..60 -> 30
                in 61..64 -> 34
                65, 66 -> 35
                in 67..70 -> 36
                in 71..74 -> 44
                75, 76 -> 45
                in 77..80 -> 46
                else -> 0 // Default case, if level is out of range
            }
            totalValue += scalar
        }

        baseAttributes.power = totalValue
        baseAttributes.toughness = totalValue
        baseAttributes.precision = totalValue
        baseAttributes.vitality = totalValue

        return baseAttributes
    }

    private fun calculateInfixUpgrades(item: Item): CharacterAttributes{

        val equipmentAttribute = CharacterAttributes()

        item.details.infix_upgrade?.attributes?.forEach{ attributeModifier ->
            when (attributeModifier.attribute) {
                "Precision" -> equipmentAttribute.precision += attributeModifier.modifier
                "Toughness" -> equipmentAttribute.toughness += attributeModifier.modifier
                "Vitality" -> equipmentAttribute.vitality += attributeModifier.modifier
                "Ferocity" -> equipmentAttribute.ferocity += attributeModifier.modifier
                "ConditionDamage" -> equipmentAttribute.conditionDamage += attributeModifier.modifier
                "Expertise" -> equipmentAttribute.expertise += attributeModifier.modifier
                "Concentration" -> equipmentAttribute.concentration += attributeModifier.modifier
                "HealingPower" -> equipmentAttribute.healingPower += attributeModifier.modifier
                "Power" -> equipmentAttribute.power += attributeModifier.modifier
            }
        }
        return equipmentAttribute
    }

    private fun addAttributes(attr1: CharacterAttributes, attr2: CharacterAttributes ){
        attr1.defense += attr2.defense
        attr1.power += attr2.power
        attr1.toughness += attr2.toughness
        attr1.vitality += attr2.vitality
        attr1.concentration += attr2.concentration
        attr1.healingPower += attr2.healingPower
        attr1.precision += attr2.precision
        attr1.conditionDamage += attr2.conditionDamage
        attr1.expertise += attr2.expertise
        attr1.ferocity += attr2.ferocity
    }


}