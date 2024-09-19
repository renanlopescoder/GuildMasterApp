package com.ai.guildmasterapp.ui.dashboard

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.R
import com.ai.guildmasterapp.R.string
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.w3c.dom.Text
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
    val infusion_slots: List<WeaponInfusionSlot> = emptyList(),
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
data class WeaponInfusionSlot(
    val flags: List<String> = emptyList()
)

@Serializable
data class InfixAttribute(
    val attribute: String = "",
    val modifier: Int = 0
)

//Adapter for the RecycleView and SearchView
class WeaponAdapter(
    private val context: Context,
    private var weaponList: List<Weapon>
) : RecyclerView.Adapter<WeaponAdapter.ItemViewHolder>(), Filterable {

    private var filteredWeaponList: List<Weapon> = weaponList
    private var onItemClickListener: ((Weapon) -> Unit)? = null

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemIcon: ImageView = view.findViewById(R.id.item_icon)
        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemDescription: TextView = view.findViewById(R.id.item_description)
        val weaponStrength: TextView = view.findViewById(R.id.weapon_strength)
        val weaponAttribute: TextView = view.findViewById(R.id.item_attributes)
        val weaponRarity: TextView = view.findViewById(R.id.weapon_rarity)
        val weaponType: TextView = view.findViewById(R.id.weapon_type)
        val weaponLevel: TextView =  view.findViewById(R.id.weapon_level)
        val weaponFlags: TextView = view.findViewById(R.id.item_flags)
        val weaponValue: TextView = view.findViewById(R.id.item_value)
        val valueIcon: ImageView = view.findViewById(R.id.value_icon)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_weapon, parent, false)
        return ItemViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val weapon = filteredWeaponList[position]


        Picasso.get().load(weapon.icon).into(holder.itemIcon)
        holder.itemName.text = weapon.name

        if (weapon.description.isEmpty()){
            holder.itemDescription.visibility = View.GONE
        }else {
            View.VISIBLE
            val plainDescription = weapon.description.replace("<.*?>".toRegex(), "")
            holder.itemDescription.text = plainDescription
        }

        if (weapon.details.max_power == 0 || weapon.details.min_power == 0) {
            holder.weaponStrength.visibility = View.GONE
        }else{
            val weaponStrengthText: String = buildString {
                append(context.getString(string.weapon_strength))
                append("${weapon.details.min_power} - ${weapon.details.max_power}")
            }
            holder.weaponStrength.text = weaponStrengthText
        }


        if (weapon.details.infix_upgrade.attributes.isEmpty()){
            holder.weaponAttribute.visibility = View.GONE
        }else{
            val formattedAttributes: String = weapon.details.infix_upgrade.attributes.let { formatAttributes(it) }.toString()
            holder.weaponAttribute.text = formattedAttributes
        }



        holder.weaponRarity.text = weapon.rarity
        when (weapon.rarity){
            "Junk" -> holder.weaponRarity.setTextColor(Color.GRAY)
            "Basic" -> holder.weaponRarity.setTextColor(Color.WHITE)
            "Fine" -> holder.weaponRarity.setTextColor(Color.CYAN)
            "Masterwork" -> holder.weaponRarity.setTextColor(Color.GREEN)
            "Rare" -> holder.weaponRarity.setTextColor(Color.YELLOW)
            "Exotic" -> holder.weaponRarity.setTextColor(Color.parseColor("#eb911c"))
            "Ascended" -> holder.weaponRarity.setTextColor(Color.parseColor("#74107d"))
            "Legendary" -> holder.weaponRarity.setTextColor(Color.parseColor("#a976ad"))
        }

        holder.weaponType.text = weapon.details.type

        if (weapon.level == 0){
            holder.weaponLevel.visibility = View.GONE
        }else{
            holder.weaponLevel.text = context.getString(R.string.weapon_level) + weapon.level.toString()
        }

        if (weapon.flags.isEmpty()){
            holder.weaponFlags.visibility = View.GONE
        }else{
            val formattedFlags = formatFlags(weapon.flags)
            holder.weaponFlags.text = formattedFlags
        }



        holder.weaponValue.text = weapon.vendor_value.toString()


        when {
            weapon.vendor_value < 100 -> holder.valueIcon.setImageResource(R.drawable.copper_coin)
            weapon.vendor_value in 100..10_000 -> holder.valueIcon.setImageResource(R.drawable.silver_coin)
            weapon.vendor_value > 10000 -> holder.valueIcon.setImageResource(R.drawable.gold_coin)
        }



        // Handle item click
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(weapon)
        }
    }

    override fun getItemCount(): Int {
        return filteredWeaponList.size
    }

    fun setOnItemClickListener(listener: (Weapon) -> Unit) {
        onItemClickListener = listener
    }

    private fun formatAttributes(attributes: List<InfixAttribute>): String {
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
                    filterResults.values = weaponList
                    filterResults.count = weaponList.size
                } else {
                    val filteredList = weaponList.filter {
                        it.name.contains(constraint, ignoreCase = true)
                    }
                    filterResults.values = filteredList
                    filterResults.count = filteredList.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredWeaponList = results?.values as List<Weapon>
                notifyDataSetChanged() // Notify RecyclerView about the updated data
            }
        }
    }
}


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

    private var mainHandList: MutableList<Weapon> = mutableListOf()
    private var offHandList: MutableList<Weapon> = mutableListOf()


    private val weaponList: MutableMap<String,Weapon?> = mutableMapOf(
        "WeaponA1" to null,
        "WeaponA2" to null
    )

    private val characterAttributes = CharacterAttributes()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private var baseAttributes = CharacterAttributes()

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
        baseAttributes = GlobalState.characterDetail?.level?.let { calculateBaseAttributes(it) }!!


        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {

                mapWeaponToSlots()



                val weaponAttributes = processWeapons(weaponList)

                addAttributes(characterAttributes,weaponAttributes)





                baseAttributes?.let { addAttributes(characterAttributes, it) }





                val mainHandWeapons= arrayOf("Axe","Dagger","Mace","Pistol","Scepter","Sword")
                val offHandWeapons= arrayOf("Dagger","Focus","Pistol","Shield","Torch","Warhorn")

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
                    mainHand.setOnClickListener{
                        val clickedImage = it as ImageView
                        showWeaponSelectionDialog(this@WeaponsBuildCalcActivity,mainHandWeapons,clickedImage, mainHandList)
                    }
                    offHand.setOnClickListener{
                        val clickedImage = it as ImageView
                        showWeaponSelectionDialog(this@WeaponsBuildCalcActivity,offHandWeapons,clickedImage,offHandList)
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
        setAttribute(defenseAttribute, string.defense_attr, attributes.defense)
        setAttribute(powerAttribute, string.power_attribute, attributes.power)
        setAttribute(toughnessAttribute, string.toughness_attribute, attributes.toughness)
        setAttribute(precisionAttribute, string.precision_attribute, attributes.precision)
        setAttribute(vitalityAttribute, string.vitality_attribute, attributes.vitality)
        setAttribute(concentrationAttribute, string.concentration_attribute, attributes.concentration)
        setAttribute(conditionDamageAttribute, string.condition_attribute, attributes.conditionDamage)
        setAttribute(expertiseAttribute, string.expertise_attribute, attributes.expertise)
        setAttribute(ferocityAttribute, string.ferocity_attribute, attributes.ferocity)
        setAttribute(healingPowerAttribute, string.healing_power_attribute, attributes.healingPower)
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

    private fun showPopupWindow(equipmentList: List<Weapon>, icon: ImageView) {

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
        val searchView: androidx.appcompat.widget.SearchView? = popupView.findViewById<androidx.appcompat.widget.SearchView>(R.id.search_view)

        recyclerView.setRecycledViewPool(RecyclerView.RecycledViewPool())
        searchView?.queryHint = "Search for weapon...."


        // Set up the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = WeaponAdapter(this, equipmentList)
        recyclerView.adapter = adapter

        // Set up item click listener manually for RecyclerView
        adapter.setOnItemClickListener { selectedItem ->
            selectedItem.icon?.let {
                //Update the icon
                loadImageWithPicasso(icon, it)

//                // remove item from itemList and add the selected item
                when (icon) {
                    mainHand -> {
                        weaponList["WeaponA1"] = selectedItem
                    }
                    offHand -> {
                        weaponList["WeaponA2"] = selectedItem
                    }
                }
//
//                // Get new attributes from item changed
                val newEquipmentAttributes = processWeapons(weaponList)
//
//                // Get new defense from item changed
//                newEquipmentAttributes.defense = (calculateInitialDefense() + baseAttributes.toughness)
//
//                // Add the new equipment Attributes with the characters Base attributes
                addAttributes(newEquipmentAttributes, baseAttributes)
//
//                //Update the UI with the new attributes
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

        popupWindow.showAtLocation(findViewById(R.id.activity_weapon_calc), Gravity.CENTER, 0, 0)
    }

    private fun fetchWeapons(
        itemList: MutableList<Weapon>, // The list to add items to
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

                    val weapon = document.toObject(Weapon::class.java)
                    itemList.add(weapon)
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
        weaponList: MutableList<Weapon>,
        collectionName: String,
        onComplete: (Boolean) -> Unit
    ) {
            // Check if the list is not empty
            if (weaponList.isNotEmpty()) {
                // Skip fetching and directly show the popup
                weaponList.clear()

                fetchWeapons(weaponList, collectionName) { success ->
                    if (success) {
                        showPopupWindow(weaponList, imageView)
                        println("Successfully added $collectionName list")
                    } else {
                        println("Failure to load $collectionName list")
                    }
                    onComplete(success)
                }
            } else {
                // Fetch the data if the list is empty
                fetchWeapons(weaponList, collectionName) { success ->
                    if (success) {
                        showPopupWindow(weaponList, imageView)
                        println("Successfully added $collectionName list")
                    } else {
                        println("Failure to load $collectionName list")
                    }
                    onComplete(success)
                }
            }

    }


    private fun showWeaponSelectionDialog(context: Context, items: Array<String>, clickedImage:ImageView, weaponsList: MutableList<Weapon>) {
        // Variable to hold the selected weapon type
        var selectedWeapon: String? = null

        // Create an AlertDialog.Builder instance
        val builder = AlertDialog.Builder(context)

        // Set the title of the dialog
        builder.setTitle("Select Weapon Type")

            // Set up the single-choice items in the dialog
            .setSingleChoiceItems(items, -1) { dialog, which ->
                // Update the selectedWeapon variable when an item is clicked
                selectedWeapon = items[which]
            }

        // Set up the positive button ("OK") for the dialog
        builder.setPositiveButton("OK") { dialog, _ ->
            // Call setupClickListener to handle the selection and update the ImageView and weaponsList
            setupClickListener(clickedImage, weaponsList, selectedWeapon.toString()) { success ->
                // Print a message if the setupClickListener function fails
                if (!success) {
                    println("Failure to load weapons")
                }
            }
            // Dismiss the dialog after the positive button is clicked
            dialog.dismiss()
        }

        // Set up the negative button ("Cancel") for the dialog
        builder.setNegativeButton("Cancel") { dialog, _ ->
            // Just dismiss the dialog if the cancel button is clicked
            dialog.dismiss()
        }

        // Create and show the dialog
        builder.create().show()
    }
}