package com.ai.guildmasterapp.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

@Serializable
data class Weapon @JvmOverloads constructor(
    val name: String = "",
    val description: String = "",
    val type: String = "",
    val level: Int = 0,
    val rarity: String = "",
    val vendor_value: Int = 0,
    val default_skin: Int = 0,
    val game_types: List<String> = emptyList(),
    val flags: List<String> = emptyList(),
    val restrictions: List<String> = emptyList(),
    val id: Int = 0,
    val chat_link: String = "",
    val icon: String = "",
    val details: WeaponDetails = WeaponDetails()
)
@Serializable
data class WeaponDetails(
    val type: String = "",
    val damage_type: String = "",
    val min_power: Int = 0,
    val max_power: Int = 0,
    val defense: Int = 0,
    val infusion_slots: List<String> = emptyList(),
    val attribute_adjustment: Float = 0.0f,
    val infix_upgrade: WeaponInfixUpgrade = WeaponInfixUpgrade(),
    val secondary_suffix_item_id: String = ""
)
@Serializable
data class WeaponInfixUpgrade(
    val id: Int = 0,
    val attributes: List<InfixAttribute> = emptyList()
)

@Serializable
data class InfixAttribute(
    val attribute: String = "",
    val modifier: Int = 0
)

class WeaponsBuildCalcActivity : AppCompatActivity() {

    // Attributes
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

    private lateinit var mainHand: ImageView
    private lateinit var offHand: ImageView

    private val weaponList: MutableMap<String,Weapon?> = mutableMapOf(
        "WeaponA1" to null,
        "WeaponA2" to null
    )

    val characterAttributes = CharacterAttributes()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weapon_calculator)
        supportActionBar?.hide()

        //Back button, when pressed it will close the activity
        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener { finish() }

        // Text Views, they are the attributes
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

        //Images
        mainHand = findViewById(R.id.main_hand)
        offHand = findViewById(R.id.off_hand)



        //Attributes calculated base on the users level
        val baseAttributes = GlobalState.characterDetail?.level?.let { calculateBaseAttributes(it) }


        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {

                mapWeaponToSlots()

                println(weaponList)

                val weaponAttributes = processWeapons(weaponList)

                addAttributes(characterAttributes,weaponAttributes)

                baseAttributes?.let { addAttributes(characterAttributes, it) }


                runOnUiThread{
                    if (baseAttributes != null) {
                        updateAttributes(characterAttributes)
                    }

                    weaponList["WeaponA1"]?.let { weapon->
                        loadImageWithPicasso(mainHand,weapon.icon)
                    }

                    weaponList["WeaponA2"]?.let { weapon->
                        loadImageWithPicasso(mainHand,weapon.icon)
                    }



                }
            }
        }



    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setAttribute(attributeTextView: TextView, attributeResId: Int, newValue: Int) {
        val attributeString = getString(attributeResId) + newValue.toString()
        attributeTextView.text = attributeString
    }

    private fun updateAttributes(attributes: CharacterAttributes) {
        setAttribute(defenseAttribute, R.string.defense_attr, attributes.defense)
        setAttribute(powerAttribute, R.string.power_attribute, attributes.power)
        setAttribute(toughnessAttribute, R.string.toughness_attribute, attributes.toughness)
        setAttribute(precisionAttribute, R.string.precision_attribute, attributes.precision)
        setAttribute(vitalityAttribute, R.string.vitality_attribute, attributes.vitality)
        setAttribute(concentrationAttribute, R.string.concentration_attribute, attributes.concentration)
        setAttribute(conditionDamageAttribute, R.string.condition_attribute, attributes.conditionDamage)
        setAttribute(expertiseAttribute, R.string.expertise_attribute, attributes.expertise)
        setAttribute(ferocityAttribute, R.string.ferocity_attribute, attributes.ferocity)
        setAttribute(healingPowerAttribute, R.string.healing_power_attribute, attributes.healingPower)
    }

    private fun calculateBaseAttributes(level: Int): CharacterAttributes {

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

    private fun loadImageWithPicasso(imageView: ImageView, url: String) {
//        Picasso.get().load(url).into(imageView)
        Picasso.get()
            .load(url)
            .fit()
            .into(imageView)
    }

    private suspend fun mapWeaponToSlots(){
        val characterDetail = GlobalState.characterDetail

        if (characterDetail != null) {
            val equipmentList = characterDetail.equipment

            for (equipment in equipmentList) {
                if (weaponList.containsKey(equipment.slot)) {
                    println("Found matching slot: ${equipment.slot}, fetching weapon...")

                    val weapon = fetchWeaponDetails(equipment.id)

                    weaponList[equipment.slot] = weapon

                    println("Mapped weapon: ${weapon?.name} to slot: ${equipment.slot}")

                }
            }


        }else{
            println("Character details or equipment list is null")
        }
    }

    private suspend fun fetchWeaponDetails(itemId: Int): Weapon? {
        val maxRetries = 3
        var currentAttempt = 0
        val json = Json { ignoreUnknownKeys = true }

        while (currentAttempt < maxRetries) {
            try {
                val request = Request.Builder()
                    .url("https://api.guildwars2.com/v2/items/$itemId")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        println("Failed to fetch item with id $itemId: ${response.message}")
                        return null
                    }

                    response.body?.string()?.let { jsonString ->
                        return try {
                            val item = json.decodeFromString<Weapon>(jsonString)
                            item // Return the fetched weapon
                        } catch (e: Exception) {
                            println("Failed to parse item JSON: ${e.message}")
                            null
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

        return null
    }

    private fun addAttributes(attr1: CharacterAttributes, attr2: CharacterAttributes) {
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

    private fun processWeapons(weaponList: MutableMap<String,Weapon?>): CharacterAttributes {
        // Initialize a new CharacterAttributes object to store the accumulated attributes
        val totalAttributes = CharacterAttributes()

        for ((key,weapon) in weaponList) {
            println("ProcessWeapons: $weapon")
            val attributes = weapon?.let { calculateInfixUpgrades(it) }
            attributes?.let { addAttributes(totalAttributes, it) }
        }

        // Return the accumulated attributes
        return totalAttributes
    }

    private fun calculateInfixUpgrades(weapon: Weapon): CharacterAttributes {

        val equipmentAttribute = CharacterAttributes()

        weapon.details.infix_upgrade.attributes.forEach { attributeModifier ->
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
}