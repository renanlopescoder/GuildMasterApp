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
    val secondary_suffix_item_id: String?,
    var attribute_adjustment_object: AttributeAdjustment? = null
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
data class AttributeAdjustment(
    val id: Int = 0,
    val name: String = " ",
    val attributes: List<AdjustmentAttributes>
)
@Serializable
data class AdjustmentAttributes(
    val attribute: String = " ",
    val multiplier: Double = 0.0,
    val value: Int = 0
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
    var baseAttributes = CharacterAttributes


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
    ////////////////////////////////////////////////////////////////
    //------------------DELETE AFTER TESTING----------------------//
    ////////////////////////////////////////////////////////////////


        val player = GlobalState.characterDetail

        val armorList = listOf(
            "Helm",
            "Shoulders",
            "Coat",
            "Boots",
            "Gloves",
            "Leggings"
        )

        val playerEquipment = player?.equipment

        CoroutineScope(Dispatchers.IO).launch {
            for (item in playerEquipment!!) {
                if (item.slot in armorList) {fetchAndAddItem(item.id)
                }
            }

            for (item in itemList) {
                item.details.infix_upgrade?.id?.let { fetchAttributeAdjustments(it,item) }
            }

            var baseAttributes = calculateBaseAttributes(player.level)

            //loop through each attribute, in each piece of equipment.

            for (item in itemList) {
                item.details.attribute_adjustment_object?.name?.forEach { _ ->
                    when (item.details.attribute_adjustment_object?.name) {
                        "Power" -> { println(item.details.attribute_adjustment_object?.name)

                        }
                        "Toughness" -> {println(item.details.attribute_adjustment_object?.name)

                        }
                        "Precision" -> {println(item.details.attribute_adjustment_object?.name)

                        }
                        "Vitality" -> {println(item.details.attribute_adjustment_object?.name)

                        }
                        "BoonDuration" -> {println(item.details.attribute_adjustment_object?.name)

                        }
                        "ConditionDamage" -> {println(item.details.attribute_adjustment_object?.name)

                        }
                        "ConditionDuration" -> {println(item.details.attribute_adjustment_object?.name)

                        }
                        "CritDamage" -> {println(item.details.attribute_adjustment_object?.name)

                        }
                        "Healing" -> {println(item.details.attribute_adjustment_object?.name)

                        }

                    }
                }

            }

            baseAttributes.defense = calculateInitialDefense();






            runOnUiThread {
                itemList.forEach{item ->

                    when (item.details.type) {
                        "Helm" -> {
                            loadImageWithPicasso(helmImage,item.icon)
                        }
                        "Shoulders" -> {
                            loadImageWithPicasso(shoulderImage,item.icon)

                        }
                        "Coat" -> {
                            loadImageWithPicasso(chestImage,item.icon)
                        }
                        "Boots" -> {
                            loadImageWithPicasso(feetImage,item.icon)
                        }
                        "Gloves" -> {
                            loadImageWithPicasso(handsImage,item.icon)
                        }
                        "Leggings" -> {
                            loadImageWithPicasso(legsImage,item.icon)
                        }
                    }
                }

                setDefense(baseAttributes.defense)
                setPower(baseAttributes.power)
                setToughness(baseAttributes.toughness)
                setPrecision(baseAttributes.precision)
                setVitality(baseAttributes.vitality)

            }

        }




    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////



    }
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
        // These four Attributes have a base value based on the characters level
        // they increment by 12 points per level
        val baseAttributes = CharacterAttributes()

        baseAttributes.power = 37 + (level - 1) * 12
        baseAttributes.toughness = 37 + (level - 1) * 12
        baseAttributes.precision = 37 + (level - 1) * 12
        baseAttributes.vitality = 37 + (level - 1) * 12

        return baseAttributes
    }

    private fun fetchAttributeAdjustments(itemId: Int, mItem: Item){
        val maxRetries = 3
        var currentAttempt = 0

        val json = Json{ignoreUnknownKeys = true}

        while (currentAttempt < maxRetries) {
            try {
                val request = Request.Builder()
                    .url("https://api.guildwars2.com/v2/itemstats/$itemId")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        println("Failed to fetch item with id $itemId: ${response.message}")
                        return@use
                    }

                    response.body?.string()?.let { jsonString ->
                        try {

                            val item = json.decodeFromString<AttributeAdjustment>(jsonString)
                            mItem.details.attribute_adjustment_object = item
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

    private fun calculateAttribute(attributeAdjustment: Double, multiplier: Double, offset:Double): Int{
        return (attributeAdjustment * multiplier + offset).toInt()
    }

    private fun applyAttributeAdjustment(baseAttributes: CharacterAttributes,item:Item){
        val totalAttributes = baseAttributes.copy()

        item.details?.let{details ->
            val attributeAdjustment = details.attribute_adjustment


        }
    }


    private fun applyInfixUpgrades(baseAttributes: CharacterAttributes, item: Item): CharacterAttributes{
        val totalAttributes = baseAttributes.copy()

        item.details.infix_upgrade?.attributes?.forEach{ attributeModifier ->
            when (attributeModifier.attribute) {
                "Power" -> totalAttributes.power += attributeModifier.modifier
                "Precision" -> totalAttributes.precision += attributeModifier.modifier
                "Toughness" -> totalAttributes.toughness += attributeModifier.modifier
                "Vitality" -> totalAttributes.vitality += attributeModifier.modifier
                "Ferocity" -> totalAttributes.ferocity += attributeModifier.modifier
                "ConditionDamage" -> totalAttributes.conditionDamage += attributeModifier.modifier
                "Expertise" -> totalAttributes.expertise += attributeModifier.modifier
                "Concentration" -> totalAttributes.concentration += attributeModifier.modifier
                "HealingPower" -> totalAttributes.healingPower += attributeModifier.modifier
            }
        }
        return totalAttributes
    }
}