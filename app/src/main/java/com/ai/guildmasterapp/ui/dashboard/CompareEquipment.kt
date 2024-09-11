package com.ai.guildmasterapp.ui.dashboard

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit



//@Serializable
//data class Item(
//    val id: Int = 0,
//    val chat_link: String = "",
//    val name: String = "",
//    val icon: String = "",
//    val description: String = "",
//    val type: String = "",
//    val rarity: String = "",
//    val level: Int = 0,
//    val vendor_value: Int = 0,
//    val default_skin: Int = 0,
//    val flags: List<String>? = null,
//    val game_types: List<String>? = null,
//    val restrictions: List<String>? = null,
//    val details: ItemDetails? = null
//)

@Serializable
data class Item(
    val id: Int = 0,
    val chat_link: String = "",
    val name: String = "",
    val icon: String? = "",
    val description: String? = "",
    val type: String = "",
    val rarity: String = "",
    val level: Int = 0,
    val vendor_value: Int = 0,
    val default_skin: Int? = 0,
    val flags: List<String> = emptyList(),
    val game_types: List<String> = emptyList(),
    val restrictions: List<String> = emptyList(),
    val details: ItemDetails? = null
)

@Serializable
data class ItemDetails(
    val type: String = "",
    val weight_class: String? = "",
    val defense: Int = 0,
    val attribute_adjustment: Double = 0.0,
    val infusion_slots: List<InfusionSlot>? = emptyList(),
    val infix_upgrade: InfixUpgrade? = InfixUpgrade(),
    val suffix_item_id: Int? = 0,
    val secondary_suffix_item_id: String? = "",
    val stat_choices: List<Int>? = emptyList()
)

@Serializable
data class InfusionSlot(
    val flags: List<String> = emptyList()
)

//@Serializable
//data class ItemDetails(
//    val type: String = "",
//    val weight_class: String = "",
//    val defense: Int = 0,
//    val attribute_adjustment: Double = 0.0,
//    val infix_upgrade: InfixUpgrade? = null,
//    val suffix_item_id: Int = 0,
//    val secondary_suffix_item_id: Int = 0,
//    val infusion_slots: List<Any>
//)

@Serializable
data class InfixUpgrade(
    val id: Int = 0,
    val attributes: List<Attribute> = emptyList(),
    val buff: Buff? = Buff()
)

@Serializable
data class Buff(
    val skill_id: Int = 0,
    val description: String = ""
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





    private var helmsList: MutableList<Item> = mutableListOf()
    private var shouldersList: MutableList<Item> = mutableListOf()
    private var coatsList: MutableList<Item> = mutableListOf()
    private var glovesList: MutableList<Item> = mutableListOf()
    private var leggingsList: MutableList<Item> = mutableListOf()
    private var bootsList: MutableList<Item> = mutableListOf()

    // This is a list of strings that is used to sort any equipment the character has DO NOT CHANGE
    private val armorTypes = listOf(
        "Helm",
        "Shoulders",
        "Coat",
        "Gloves",
        "Leggings",
        "Boots"
    )

    private val client = OkHttpClient.Builder()
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


        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {

                for (item in GlobalState.characterDetail?.equipment!!) {
                    if (item.slot in armorTypes) {
                        fetchAndAddItem(item.id)
                    }
                }

//              Calculate the base attributes based on the characters level and store them in baseAttributes
                val baseAttributes = GlobalState.characterDetail?.level?.let { calculateBaseAttributes(it) }

                // Will be deleted once firestore is up and running with the data


                val characterAttributes = CharacterAttributes()

                processArmorItems(itemList, characterAttributes)

                if (baseAttributes != null) addAttributes(characterAttributes, baseAttributes)

//      Calculate the defense by adding all the equipments defense values together, and add toughness from baseAttributes
                characterAttributes.defense = (calculateInitialDefense() + baseAttributes?.toughness!!)

                runOnUiThread {

//              Loop through itemList and match the item to the Equipment slot, update image using Picasso
                    itemList.forEach { item ->
                        when (item.details?.type) {
                            "Helm" -> item?.icon?.let { loadImageWithPicasso(helmImage, it) }
                            "Shoulders" -> item.icon?.let { loadImageWithPicasso(shoulderImage, it) }
                            "Coat" -> item.icon?.let { loadImageWithPicasso(chestImage, it) }
                            "Boots" -> item.icon?.let { loadImageWithPicasso(feetImage, it) }
                            "Gloves" -> item.icon?.let { loadImageWithPicasso(handsImage, it) }
                            "Leggings" -> item.icon?.let { loadImageWithPicasso(legsImage, it) }
                        }
                    }




                    updateAttributes(characterAttributes)

                    allowToContinue()

                    setupClickListener(helmImage, helmsList, "helmtest") { success ->
                        if (!success) {
                            println("Failure to load helmsList")
                        }
                    }

                    setupClickListener(shoulderImage, shouldersList, "shoulders") { success ->
                        if (!success) {
                            println("Failure to load shoulders list")
                        }
                    }

                    setupClickListener(chestImage, coatsList, "coats") { success ->
                        if (!success) {
                            println("Failure to load coat list")
                        }
                    }

                    setupClickListener(handsImage, glovesList, "hands") { success ->
                        if (!success) {
                            println("Failure to load gloves list")
                        }
                    }

                    setupClickListener(legsImage, leggingsList, "leggings") { success ->
                        if (!success) {
                            println("Failure to load legs list")
                        }
                    }

                    setupClickListener(feetImage, bootsList, "boots") { success ->
                        if (!success) {
                            println("Failure to load boots list")
                        }
                    }
                }
            }
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun returnToMain() {
        finish()
    }

    private fun calculateInitialDefense(): Int {
        var totalEquipmentDefense: Int = 0

        for (item in itemList) {
            val defense = item.details?.defense ?: 0
            totalEquipmentDefense += defense
        }

        return totalEquipmentDefense
    }

    private fun processArmorItems(itemList: List<Item>, characterAttributes: CharacterAttributes) {

        for (item in itemList) {
            if (item.details?.type in armorTypes) {
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

        item.details?.infix_upgrade?.attributes?.forEach { attributeModifier ->
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

    private fun fetchEquipment(
        itemList: MutableList<Item>, // The list to add items to
        collectionName: String,      // The Firestore collection name
        onComplete: (Boolean) -> Unit // Callback to notify when the task is completed
    ) {
        // Get a reference to the Firestore instance
        val db = FirebaseFirestore.getInstance()

        // Check if the list is empty, reduces multiples of data
        if (itemList.isNotEmpty()){
            onComplete(true)
            Log.d(TAG, " is not empty.")
            return
        }

        // Query the collection based on the collection name passed
        db.collection(collectionName)
            .get()
            .addOnSuccessListener { documents ->
                // Loop through each document in the query result
                for (document in documents) {

                    val item = document.toObject(Item::class.java)
                    itemList.add(item)
                }

                // Notify that the task is completed successfully
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
                println("Error fetching documents: ${exception.message}")
                // Notify that the task failed
                onComplete(false)
            }
    }

    private fun setupClickListener(
        imageView: ImageView,
        itemList: MutableList<Item>,
        collectionName: String,
        onComplete: (Boolean) -> Unit
    ) {
        imageView.setOnClickListener {
            // Check if the list is not empty
            if (itemList.isNotEmpty()) {
                // Skip fetching and directly show the popup
                showPopupWindow(itemList, imageView)
                println("Successfully added ${collectionName} list")
                onComplete(true)
            } else {
                // Fetch the data if the list is empty
                fetchEquipment(itemList, collectionName) { success ->
                    if (success) {
                        showPopupWindow(itemList, imageView)
                        println("Successfully added ${collectionName} list")
                    } else {
                        println("Failure to load ${collectionName} list")
                    }
                    onComplete(success)
                }
            }
        }
    }
}