package com.ai.guildmasterapp.ui.dashboard

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.R
import com.ai.guildmasterapp.databinding.ActivityMainBinding
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import java.io.InputStreamReader


@Serializable
data class Item(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val type: String = "",
    val level: Int = 0,
    val icon: String = "",
    val details: ItemDetails
)

@Serializable
data class ItemDetails(
    val type: String = "",
    val defense: Int = 0,
    val attribute_adjustment: Double = 0.0,
    val infix_upgrade: InfixUpgrade? = null
    )
@Serializable
data class InfixUpgrade(
    val id: Int = 0,
    val attributes: List<Attribute>? = null
)
@Serializable
data class Attribute(
    val attribute: String = "",
    val modifier: Int = 0
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

class ItemAdapter(context: Context, private val itemList: List<Item>) :
    ArrayAdapter<Item>(context, 0, itemList), Filterable {

    private var filteredItemList: List<Item> = itemList

    override fun getCount(): Int {
        return filteredItemList.size
    }

    override fun getItem(position: Int): Item? {
        return filteredItemList[position]
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_item, parent, false
        )

        val item = getItem(position)
        val textView = view.findViewById<TextView>(R.id.text)
        val subTextView = view.findViewById<TextView>(R.id.subtext)

        textView.text = item?.name
        subTextView.text = "Level: ${item?.level.toString()}"


        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint.isNullOrEmpty()) {
                    filterResults.values = itemList
                    filterResults.count = itemList.size
                } else {
                    val filteredList = itemList.filter {
                        it.name.contains(constraint, ignoreCase = true)
                    }
                    filterResults.values = filteredList
                    filterResults.count = filteredList.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItemList = results?.values as List<Item>
                notifyDataSetChanged()
            }
        }
    }
}

class CompareEquipment : AppCompatActivity() {

    // Armor Icons
    private lateinit var helmImage: ImageView
    private lateinit var shoulderImage: ImageView
    private lateinit var chestImage: ImageView
    private lateinit var handsImage: ImageView
    private lateinit var legsImage: ImageView
    private lateinit var feetImage: ImageView

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

    // Loading Screen
    private lateinit var loadingScreen: ConstraintLayout
    private lateinit var continuePrompt: TextView
    private lateinit var loadingBar: ProgressBar

    //Popup Window
    private lateinit var helmList: List<Item>
    private lateinit var helms: List<String>
    private lateinit var shoulders: List<String>
    private lateinit var coats: List<String>
    private lateinit var hands: List<String>
    private lateinit var leggings: List<String>
    private lateinit var feet: List<String>

    private lateinit var adapter : ArrayAdapter<String>


    // This is a list of strings that is used to sort any equipment the character has DO NOT CHANGE
    val armorList = listOf(
        "Helm",
        "Shoulders",
        "Coat",
        "Boots",
        "Gloves",
        "Leggings"
    )


    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val itemList = mutableListOf<Item>()
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

        loadingScreen = findViewById(R.id.loading_screen)
        continuePrompt = findViewById(R.id.continue_prompt)
        loadingBar = findViewById(R.id.splashscreen_loadingbar)
///////////////////////////////////////////////////////////////////////////////////////////////
//      TESTING ONLY
        helms = listOf("Abaddon's Mask", "Bloodstone Visage", "Dragon's Mask")
        shoulders = listOf("Ancient Canthan Spaulders", "Deathly Pauldrons", "Foe fire Mantle")
        coats = listOf("Coat", "Coat Coat")
        hands = listOf("hands", "hands hands")
        leggings = listOf("legs", "leggings", "legs hands")
        feet = listOf("feet", "foot", "toes", "heel","feet", "foot", "toes", "heel","feet", "foot", "toes", "heel","feet", "foot", "toes", "heel","feet", "foot", "toes", "heel","feet", "foot", "toes", "heel","feet", "foot", "toes", "heel")


///////////////////////////////////////////////////////////////////////////////////////////////

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                for (item in GlobalState.characterDetail?.equipment!!) {
                    if (item.slot in armorList) {
                        fetchAndAddItem(item.id)
                    }
                }

//      Calculate the base attributes based on the characters level and store them in baseAttributes
                val baseAttributes = GlobalState.characterDetail?.level?.let { calculateBaseAttributes(it) }

                helmList = loadHelms(this@CompareEquipment,R.raw.helm)

                val characterAttributes = CharacterAttributes()

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

                if (baseAttributes != null) addAttributes(characterAttributes, baseAttributes)

//      Calculate the defense by adding all the equipments defense values together, and add toughness from baseAttributes
                characterAttributes.defense = (calculateInitialDefense() + baseAttributes?.toughness!!)

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

                    allowToContinue()

                    helmImage.setOnClickListener { showPopupWindow(helmList, it as ImageView) }
                    shoulderImage.setOnClickListener { showPopupWindow(helmList, shoulderImage) }
                    chestImage.setOnClickListener { showPopupWindow(helmList, chestImage) }
                    handsImage.setOnClickListener { showPopupWindow(helmList, handsImage) }
                    legsImage.setOnClickListener { showPopupWindow(helmList, legsImage) }
                    feetImage.setOnClickListener { showPopupWindow(helmList, feetImage) }
                }
            }
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun returnToMain() {
        finish()
    }

    private fun calculateInitialDefense(): Int {
        var totalEquipmentDefense: Int = 0

        for (item in itemList) {
            totalEquipmentDefense += item.details.defense
        }

        return totalEquipmentDefense
    }

    private fun setDefense(newDefense: Int) {
        val defenseString = getString(R.string.defense_attr) + newDefense.toString()
        defenseAttribute.text = defenseString
    }

    private fun setPower(newPower: Int) {
        val powerString = getString(R.string.power_attribute) + newPower.toString()
        powerAttribute.text = powerString
    }

    private fun setToughness(newToughness: Int) {
        val toString = getString(R.string.toughness_attribute) + newToughness.toString()
        toughnessAttribute.text = toString
    }

    private fun setPrecision(newPrecision: Int) {
        val precisionString = getString(R.string.precision_attribute) + newPrecision.toString()
        precisionAttribute.text = precisionString
    }

    private fun setVitality(newVitality: Int) {
        val vitalityString = getString(R.string.vitality_attribute) + newVitality.toString()
        vitalityAttribute.text = vitalityString
    }

    private fun setConcentration(newConcentration: Int) {
        val concentrationString = getString(R.string.concentration_attribute) + newConcentration.toString()
        concentrationAttribute.text = concentrationString
    }

    private fun setConditionDamage(newConditionDamage: Int) {
        val conditionString = getString(R.string.condition_attribute) + newConditionDamage.toString()
        conditionDamageAttribute.text = conditionString
    }

    private fun setExpertise(newExpertise: Int) {
        val expertiseString = getString(R.string.expertise_attribute) + newExpertise.toString()
        expertiseAttribute.text = expertiseString
    }

    private fun setFerocity(newFerocity: Int) {
        val ferocityString = getString(R.string.ferocity_attribute) + newFerocity.toString()
        ferocityAttribute.text = ferocityString
    }

    private fun setHealingPower(newHealingPower: Int) {
        val healingPowerString = getString(R.string.healing_power_attribute) + newHealingPower.toString()
        healingPowerAttribute.text = healingPowerString
    }

    suspend fun fetchAndAddItem(itemId: Int) {
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
        Picasso.get().load(url).into(imageView)
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

    private fun calculateInfixUpgrades(item: Item): CharacterAttributes {

        val equipmentAttribute = CharacterAttributes()

        item.details.infix_upgrade?.attributes?.forEach { attributeModifier ->
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



    private fun allowToContinue(){

        loadingBar.isIndeterminate = false
        loadingBar.max = 100     // Set max value for the progress bar

        loadingBar.visibility = View.VISIBLE
        loadingBar.progress = 0  // Ensure progress starts at 0


        // Create a ValueAnimator to animate the progress over 3 seconds (3000ms)
        val animator = ValueAnimator.ofInt(0, 100)
        animator.duration = 3000 // 3 seconds duration

        // Update the ProgressBar's progress on every animation frame
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            loadingBar.progress = progress
        }

        // When the animation ends, hide the progress bar and show the continue prompt
        animator.doOnEnd {
            loadingBar.visibility = View.GONE
            continuePrompt.visibility = View.VISIBLE

            // Set click listener for the loading screen
            loadingScreen.setOnClickListener {
                loadingScreen.visibility = View.GONE
            }
        }

        // Start the animation
        animator.start()
    }

    private fun showPopupWindow(equipmentList: List<Item>, icon: ImageView) {

        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_equipment_selection, null)



        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val listView: ListView = popupView.findViewById(R.id.equipment_list)
        val searchView: SearchView = popupView.findViewById(R.id.search_view)

        val adapter = ItemAdapter(this, equipmentList)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
            val selectedItem = adapter.getItem(position)
            // Handle the selected item here
            selectedItem?.icon.let{
                if (it != null) {
                    loadImageWithPicasso(icon, it)
                }
            }
            Toast.makeText(this, "Clicked: ${selectedItem?.name}", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        popupWindow.showAtLocation(findViewById(R.id.equipment_build_calc), Gravity.CENTER, 0, 0)
    }

    fun loadHelms(context: Context, fileName: Int): List<Item> {
        val helmList = mutableListOf<Item>()

        val json = Json{ ignoreUnknownKeys = true }
        try {
            val inputStream: InputStream = context.resources.openRawResource(fileName)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            helmList.addAll(json.decodeFromString(jsonString))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return helmList
    }


}

