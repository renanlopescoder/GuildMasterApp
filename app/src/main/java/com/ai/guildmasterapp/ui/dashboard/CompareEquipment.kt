@file:Suppress("KotlinConstantConditions", "PropertyName")

package com.ai.guildmasterapp.ui.dashboard

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
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import androidx.appcompat.widget.SearchView
import com.ai.guildmasterapp.LoaderDialogFragment


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


//Adapter for the RecycleView and SearchView
class ItemAdapter(
    private val context: Context,
    private var itemList: List<Item>
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>(), Filterable {

    private var filteredItemList: List<Item> = itemList
    private var onItemClickListener: ((Item) -> Unit)? = null

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = filteredItemList[position]


        Picasso.get().load(item.icon).into(holder.itemIcon)
        holder.itemName.text = item.name
        if (item.details?.defense == 0){
            holder.itemDefense.visibility = View.GONE
        } else {
            holder.itemDefense.text = context.getString(R.string.item_defense) + " ${item.details?.defense.toString()}"
        }

        if(item.details?.infix_upgrade?.attributes?.isEmpty() == true){
            holder.itemAttributes.visibility = View.GONE
        }else {
            val formattedAttributes: String =
                item.details?.infix_upgrade?.attributes?.let { formatAttributes(it) }.toString()
            holder.itemAttributes.text = formattedAttributes
        }

        if (item.details?.weight_class?.isEmpty() == true){
            holder.itemWeightClass.visibility = View.GONE
        } else {
            holder.itemWeightClass.text = item.details?.weight_class
        }
        holder.itemArmorSlot.text = item.details?.type

        if (item.flags.isEmpty()){
            holder.itemFlags.visibility = View.GONE
        } else {
            val formattedFlags: String = formatFlags(item.flags)
            holder.itemFlags.text = formattedFlags
        }
        holder.itemValue.text = item.vendor_value.toString()

        when {
            item.vendor_value < 100 -> holder.valueIcon.setImageResource(R.drawable.copper_coin)
            item.vendor_value in 100..10_000 -> holder.valueIcon.setImageResource(R.drawable.silver_coin)
            item.vendor_value > 10_000 -> holder.valueIcon.setImageResource(R.drawable.gold_coin)
        }

        if (item.description?.isEmpty() == true){
            holder.itemDescription.visibility = View.GONE
        } else {
            val plainDescription = item.description?.replace("<.*?>".toRegex(), "")
            holder.itemDescription.text = plainDescription
        }

        // Handle item click
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return filteredItemList.size
    }

    fun setOnItemClickListener(listener: (Item) -> Unit) {
        onItemClickListener = listener
    }

    private fun formatAttributes(attributes: List<Attribute>): String {
        return attributes.joinToString(separator = "\n") {
            "${it.attribute}: ${it.modifier}"
        }
    }

    private fun formatFlags(flags: List<String>): String {
        return flags.joinToString(separator = "\n")
    }

    // Filter logic
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
                filteredItemList = results?.values as? List<Item> ?: emptyList()
                notifyItemRangeChanged(0, filteredItemList.size) // Notify RecyclerView about the updated data

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
    private val characterAttributes = CharacterAttributes()

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

    //List used to store all the users Armor, but is changed when the user changes Items by using the popupwindow
    private val itemList = mutableListOf<Item>()


    //Attributes calculated base on the users level
    private val baseAttributes = GlobalState.characterDetail?.level?.let { calculateBaseAttributes(it) }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("onCreate:", "Created: ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_armor_calculator)
        supportActionBar?.hide()

        //Back button, when pressed it will close the activity
        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener { returnToMain() }

        val helpButton: ImageView = findViewById(R.id.help_button)
        helpButton.setOnClickListener{
            showHelpDialog()
        }



        // Image Views, they are the Icons
        helmImage = findViewById(R.id.equipment_helm)
        shoulderImage = findViewById(R.id.equipment_shoulders)
        chestImage = findViewById(R.id.equipment_chest)
        handsImage = findViewById(R.id.equipment_hands)
        legsImage = findViewById(R.id.equipment_legs)
        feetImage = findViewById(R.id.equipment_feet)

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

        // Loading screen widgets
//        loadingScreen = findViewById(R.id.loading_screen)



        // Use Coroutine for performance
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {


                val loader = LoaderDialogFragment.newInstance("Equipment")
                loader.isCancelable = false
                loader.show(supportFragmentManager, "Armor")

                // Loop through characters Items and push into a list
                for (item in GlobalState.characterDetail?.equipment!!) {
                    if (item.slot in armorTypes) {
                        fetchAndAddItem(item.id)
                    }
                }
                // Attributes based on the character in-game armor
                val equipmentAttributes = processArmorItems(itemList)

                // Add to Character Attributes
                addAttributes(characterAttributes,equipmentAttributes)

                // Add base attributes to character attributes
                baseAttributes?.let { addAttributes(characterAttributes, it) }

//              Calculate the defense by adding all the equipments defense values together, and add toughness from baseAttributes
                characterAttributes.defense = (calculateInitialDefense() + baseAttributes?.toughness!!)

                runOnUiThread {

//              Loop through itemList and match the item to the Equipment slot, update image using Picasso
                    itemList.forEach { item ->
                        when (item.details?.type) {
                            "Helm" -> item.icon?.let { loadImageWithPicasso(helmImage, it) }
                            "Shoulders" -> item.icon?.let { loadImageWithPicasso(shoulderImage, it) }
                            "Coat" -> item.icon?.let { loadImageWithPicasso(chestImage, it) }
                            "Boots" -> item.icon?.let { loadImageWithPicasso(feetImage, it) }
                            "Gloves" -> item.icon?.let { loadImageWithPicasso(handsImage, it) }
                            "Leggings" -> item.icon?.let { loadImageWithPicasso(legsImage, it) }
                        }
                    }

                    updateAttributes(characterAttributes)

                    //Close loading screen
                    loader.dismiss()

                    // Icon listeners, will bring up pop up window
                    val equipmentMap = mapOf(
                        helmImage to Pair(helmsList, "helmtest"),
                        shoulderImage to Pair(shouldersList, "shoulders"),
                        chestImage to Pair(coatsList, "coats"),
                        handsImage to Pair(glovesList, "hands"),
                        legsImage to Pair(leggingsList, "leggings"),
                        feetImage to Pair(bootsList, "boots")
                    )

                    equipmentMap.forEach { (imageView, pair) ->
                            setupClickListener(imageView, pair.first, pair.second) { success ->
                            if (!success) {
                                println("Failure to load ${pair.second} list")
                            }
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
        var totalEquipmentDefense = 0

        for (item in itemList) {
            val defense = item.details?.defense ?: 0
            totalEquipmentDefense += defense
        }

        return totalEquipmentDefense
    }

    private fun processArmorItems(itemList: List<Item>): CharacterAttributes {
        // Initialize a new CharacterAttributes object to store the accumulated attributes
        val totalAttributes = CharacterAttributes()

        for (item in itemList) {
            if (item.details?.type in armorTypes) {
                val attributes: CharacterAttributes = calculateInfixUpgrades(item)
                // Accumulate the calculated attributes
                addAttributes(totalAttributes, attributes)
            }
        }

        // Return the accumulated attributes
        return totalAttributes
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

    private fun fetchAndAddItem(itemId: Int) {
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
//        Picasso.get().load(url).into(imageView)
        Picasso.get()
            .load(url)
            .fit()
            .into(imageView)
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
                "CritDamage" -> equipmentAttribute.ferocity += attributeModifier.modifier
                "ConditionDamage" -> equipmentAttribute.conditionDamage += attributeModifier.modifier
                "ConditionDuration" -> equipmentAttribute.expertise += attributeModifier.modifier
                "BoonDuration" -> equipmentAttribute.concentration += attributeModifier.modifier
                "Healing" -> equipmentAttribute.healingPower += attributeModifier.modifier
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

    private fun showPopupWindow(equipmentList: List<Item>, icon: ImageView) {

        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_equipment_selection, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Use RecyclerView instead of ListView
        val recyclerView: RecyclerView = popupView.findViewById(R.id.equipment_recycler_list)
        val searchView: SearchView? = popupView.findViewById(R.id.search_view)

        recyclerView.setRecycledViewPool(RecyclerView.RecycledViewPool())
        searchView?.queryHint = "Search for equipment...."


        // Set up the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ItemAdapter(this, equipmentList)
        recyclerView.adapter = adapter

        // Set up item click listener manually for RecyclerView
        adapter.setOnItemClickListener { selectedItem ->
            selectedItem.icon?.let {
                //Update the icon
                loadImageWithPicasso(icon, it)

                // remove item from itemList and add the selected item
                selectedItem.details?.type?.let { it1 -> updateArmorItem(itemList, it1,selectedItem) }

                // Get new attributes from item changed
                val newEquipmentAttributes = processArmorItems(itemList)

                // Get new defense from item changed
                newEquipmentAttributes.defense = (calculateInitialDefense() + baseAttributes?.toughness!!)

                // Add the new equipment Attributes with the characters Base attributes
                addAttributes(newEquipmentAttributes, baseAttributes)

                //Update the UI with the new attributes
                updateAttributes(newEquipmentAttributes)
            }
            Toast.makeText(this, "Selected: ${selectedItem.name}", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        // Set up SearchView filtering
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        db.collection(collectionName).limit(50)
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
        val loader = LoaderDialogFragment.newInstance("Armor").apply {
            isCancelable = false
            show(supportFragmentManager, "Armor")
        }

        if (itemList.isNotEmpty()) {
            showPopupWindow(itemList, imageView)
            println("Successfully added $collectionName list")
            onComplete(true)
        } else {
            fetchEquipment(itemList, collectionName) { success ->
                if (success) {
                    showPopupWindow(itemList, imageView)
                    println("Successfully added $collectionName list")
                } else {
                    println("Failure to load $collectionName list")
                }
                onComplete(success)
            }
        }
        loader.dismiss()
    }
}

    private fun updateArmorItem(itemList: MutableList<Item>, armorType: String, newItem: Item) {
        // Find the index of the current item based on its details.type
        val currentIndex = itemList.indexOfFirst { it.details?.type == armorType }

        // Remove the current item if found
        if (currentIndex != -1) {
            itemList.removeAt(currentIndex)
        }
        // Add the new item
        itemList.add(newItem)
    }

    private fun showHelpDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.activity_armor_calculator_help, null)
        val dialog = AlertDialog.Builder(this)

            .setView(dialogView)
            .create()

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setGravity(Gravity.CENTER)
            setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
}

        dialogView.findViewById<Button>(R.id.close_button).setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }



}