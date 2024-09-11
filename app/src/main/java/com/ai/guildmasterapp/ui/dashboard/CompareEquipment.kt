package com.ai.guildmasterapp.ui.dashboard

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
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
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
<<<<<<< Updated upstream
import java.io.InputStream
=======
import okhttp3.internal.platform.android.AndroidLogHandler.setLevel
>>>>>>> Stashed changes
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit



@Serializable
data class Item(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val type: String = "",
    val level: Int = 0,
    val vendor_value: Int = 0,
    val flags: List<String>? = null,
    val icon: String = "",
    val details: ItemDetails
)

@Serializable
data class ItemDetails(
    val type: String = "",
    val weight_class: String = "",
    val defense: Int = 0,
    val attribute_adjustment: Double = 0.0,
<<<<<<< Updated upstream
    val infix_upgrade: InfixUpgrade? = null
    )
=======
    val infix_upgrade: InfixUpgrade?,
    val suffix_item_id: Int? = 0,
    val secondary_suffix_item_id: String?,
    var attribute_adjustment_object: AttributeAdjustment? = null
)
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
=======
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
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
=======


>>>>>>> Stashed changes

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
        val itemIcon: ImageView = view.findViewById(R.id.item_icon)
        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemDescription: TextView = view.findViewById(R.id.item_description)
        val itemDefense: TextView = view.findViewById(R.id.item_defense)
        val itemAttributes: TextView = view.findViewById(R.id.item_attributes)
        val itemWeightClass: TextView = view.findViewById(R.id.item_weightclass)
        val itemArmorSlot: TextView = view.findViewById(R.id.item_slot)
        val itemFlags: TextView = view.findViewById(R.id.item_flags)
        val itemValue: TextView = view.findViewById(R.id.item_value)
        val valueIcon: ImageView = view.findViewById(R.id.value_icon)



        if (item != null) {
            Picasso.get().load(item.icon).into(itemIcon)
        }
        itemName.text = item?.name

        itemDefense.text = item?.details?.defense.toString()
        val formattedAttributes: String? = item?.details?.infix_upgrade?.attributes?.let { formatAttributes(it) }
        itemAttributes.text = formattedAttributes
        itemWeightClass.text = item?.details?.weight_class
        itemArmorSlot.text = item?.details?.type
        val formattedFlags: String? = item?.flags?.let { formatFlags(it) }
        itemFlags.text = formattedFlags
        itemValue.text = item?.vendor_value.toString()

        when {
            item?.vendor_value!! < 100 -> valueIcon.setImageResource(R.drawable.copper_coin)
            item.vendor_value in 100..10_000 -> valueIcon.setImageResource(R.drawable.silver_coin)
            item.vendor_value > 10_000 -> valueIcon.setImageResource(R.drawable.gold_coin)
        }

        val description = item?.description
        val plainDescription = description?.replace("<.*?>".toRegex(), " ")
        itemDescription.text = plainDescription



        return view
    }

    fun formatAttributes(attributes: List<Attribute>): String {
        return attributes.joinToString(separator = "\n\n") {
            "${it.attribute} = ${it.modifier}"
        }
    }

    fun formatFlags(flags: List<String>): String{
        return flags.joinToString(separator = "\n\n")
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
<<<<<<< Updated upstream

    // Attributes
=======
    // Text Views
>>>>>>> Stashed changes
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

    // This is a list of strings that is used to sort any equipment the character has DO NOT CHANGE
    private val armorTypes = listOf(
        "Helm",
        "Shoulders",
        "Coat",
        "Boots",
        "Gloves",
        "Leggings"
    )

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

<<<<<<< Updated upstream
    private val itemList = mutableListOf<Item>()
    var baseAttributes = CharacterAttributes()
=======
    val itemList = mutableListOf<Item>()
    var baseAttributes = CharacterAttributes
>>>>>>> Stashed changes


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


        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {

<<<<<<< Updated upstream
                for (item in GlobalState.characterDetail?.equipment!!) {
                    if (item.slot in armorTypes) {
                        fetchAndAddItem(item.id)
                    }
                }
=======
        val armorList = listOf(
            "Helm",
            "Shoulders",
            "Coat",
            "Boots",
            "Gloves",
            "Leggings"
        )
>>>>>>> Stashed changes

//              Calculate the base attributes based on the characters level and store them in baseAttributes
                val baseAttributes = GlobalState.characterDetail?.level?.let { calculateBaseAttributes(it) }

<<<<<<< Updated upstream
                // Will be deleted once firestore is up and running with the data
                helmList = loadHelms(this@CompareEquipment,R.raw.helm)

                val characterAttributes = CharacterAttributes()

                processArmorItems(itemList, characterAttributes)

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
=======
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
>>>>>>> Stashed changes
                        }
                    }




                    updateAttributes(characterAttributes)

                    allowToContinue()

                    helmImage.setOnClickListener { showPopupWindow(helmList, it as ImageView) }
                    shoulderImage.setOnClickListener { showPopupWindow(helmList, shoulderImage) }
                    chestImage.setOnClickListener { showPopupWindow(helmList, chestImage) }
                    handsImage.setOnClickListener { showPopupWindow(helmList, handsImage) }
                    legsImage.setOnClickListener { showPopupWindow(helmList, legsImage) }
                    feetImage.setOnClickListener { showPopupWindow(helmList, feetImage) }
                }

                setDefense(baseAttributes.defense)
                setPower(baseAttributes.power)
                setToughness(baseAttributes.toughness)
                setPrecision(baseAttributes.precision)
                setVitality(baseAttributes.vitality)

            }

        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun returnToMain() {
        finish()
    }

<<<<<<< Updated upstream
    private fun calculateInitialDefense(): Int {
=======
    private fun calculateInitialDefense(): Int{
>>>>>>> Stashed changes
        var totalEquipmentDefense: Int = 0

        for (item in itemList) {
            totalEquipmentDefense += item.details.defense
        }

        return totalEquipmentDefense
<<<<<<< Updated upstream
=======
    }

    private fun setDefense(newDefense: Int){
        val defenseString = getString(R.string.defense_attr) + newDefense.toString()
        defenseAttribute.text = defenseString
    }

    private fun setPower(newPower: Int){
        val powerString = getString(R.string.power_attribute) + newPower.toString()
        powerAttribute.text = powerString
>>>>>>> Stashed changes
    }

    private fun processArmorItems(itemList: List<Item>, characterAttributes: CharacterAttributes) {

        for (item in itemList) {
            if (item.details.type in armorTypes) {
                val attributes: CharacterAttributes = calculateInfixUpgrades(item)
                addAttributes(characterAttributes, attributes)
            }
        }
    }

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

    private suspend fun fetchAndAddItem(itemId: Int) {
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
<<<<<<< Updated upstream
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
=======
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
>>>>>>> Stashed changes

        return baseAttributes
    }

<<<<<<< Updated upstream
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
            Toast.makeText(this, "Selected: ${selectedItem?.name}", Toast.LENGTH_SHORT).show()
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

    private fun loadHelms(context: Context, fileName: Int): List<Item> {
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

=======
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
>>>>>>> Stashed changes
