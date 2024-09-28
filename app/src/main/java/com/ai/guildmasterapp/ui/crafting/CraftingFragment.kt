package com.ai.guildmasterapp.ui.crafting

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.LAYOUT_INFLATER_SERVICE
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ai.guildmasterapp.LoaderDialogFragment
import com.ai.guildmasterapp.R
import com.ai.guildmasterapp.databinding.FragmentCraftingBinding
import com.ai.guildmasterapp.databinding.FragmentCraftingBinding.inflate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val chat_link: String = "",
    val disciplines: String? = "",
    val flags: String? = "",
    val guild_ingredients: String? = "",
    val icon: String = "",
    val id: Int = 0,
    val ingredients: MutableList<Ingredient> = mutableListOf(),
    val min_rating: Int? = 0,
    val name: String? = "",
    val output_item_count: Int? = 0,
    val output_item_id: Int? = 0,
    val output_upgrade_id: Int? = 0,
    val time_to_craft_ms: Long? = 0,
    val type: String? = ""
)

@Serializable
data class Ingredient(
    val count: Int = 0,
    val name: String = "",
    val icon: String = "",
    val item_id: Int = 0,
)


// HashMap, it maps the Name of the Recipe Type, to the collection name in the FireStore
val recipeMap = hashMapOf(
    "Amulet" to "recipes_amulet",
    "Axe" to "recipes_axe",
    "Backpack" to "recipes_backpack",
    "Bag" to "recipes_bag",
    "Boots" to "recipes_boots",
    "Bulk" to "recipes_bulk",
    "Coat" to "recipes_coat",
    "Component" to "recipes_component",
    "Consumable" to "recipes_consumable",
    "Dagger" to "recipes_dagger",
    "Dessert" to "recipes_dessert",
    "Dye" to "recipes_dye",
    "Earring" to "recipes_earring",
    "Feast" to "recipes_feast",
    "Focus" to "recipes_focus",
    "Gloves" to "recipes_gloves",
    "Greatsword" to "recipes_greatsword",
    "Guild Consumable" to "recipes_guildconsumable",
    "Guild ConsumableWvW" to "recipes_guildconsumableWvW",
    "Guild Decoration" to "recipes_guilddecoration",
    "Hammer" to "recipes_hammer",
    "Harpoon" to "recipes_harpoon",
    "Helm" to "recipes_helm",
    "Ingredient Cooking" to "recipes_ingredientcooking",
    "Inscription" to "recipes_inscription",
    "Insignia" to "recipes_insignia",
    "Legendary Component" to "recipes_legendarycomponent",
    "Leggings" to "recipes_leggings",
    "Long Bow" to "recipes_longbow",
    "Mace" to "recipes_mace",
    "Meal" to "recipes_meal",
    "Pistol" to "recipes_pistol",
    "Potion" to "recipes_potion",
    "Refinement" to "recipes_refinement",
    "Refinement Ectoplasm" to "recipes_refinementemptasm",
    "Refinement Obsidian" to "recipes_refinementobsidian",
    "Rifle" to "recipes_rifle",
    "Ring" to "recipes_ring",
    "Scepter" to "recipes_scepter",
    "Seasoning" to "recipes_seasoning",
    "Shield" to "recipes_shield",
    "Short Bow" to "recipes_shortbow",
    "Shoulders" to "recipes_shoulders",
    "Snack" to "recipes_snack",
    "Soup" to "recipes_soup",
    "Speargun" to "recipes_speargun",
    "Staff" to "recipes_staff",
    "Sword" to "recipes_sword",
    "Torch" to "recipes_torch",
    "Trident" to "recipes_trident",
    "Upgrade Component" to "recipes_upgradecomponent",
    "Warhorn" to "recipes_warhorn"
)


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Custom Adapter, for the RecyclerView that holds the Recipe Type. Needed to alter the view of each item in the list
class RecipeTypeAdapter(private val recipeTypes: Array<String>,private val itemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<RecipeTypeAdapter.RecipeViewHolder>(){
            class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val recipeTextView: TextView = itemView.findViewById(R.id.recipe_list_name)
            }

    interface OnItemClickListener {
        fun onItemClick(recipe: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_type_list, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.recipeTextView.text = recipeTypes[position]

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(recipeTypes[position])
        }
    }

    override fun getItemCount(): Int {
        return recipeTypes.size
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Custom Adapter, for the RecyclerView that holds the Recipe. Needed to alter the view of each item in the list
class RecipeAdapter(private var recipes: List<Recipe>)
    : RecyclerView.Adapter<RecipeAdapter.ViewHolder>(), Filterable {

    private var filteredRecipes: List<Recipe> = recipes
    private var onItemClickListener: ((Recipe) -> Unit)? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeNameTextView: TextView = itemView.findViewById(R.id.recipe_list_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = filteredRecipes[position]
        holder.recipeNameTextView.text = recipe.name
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(recipe)
        }
    }

    override fun getItemCount() = filteredRecipes.size

    fun setOnRecipeClickListener(listener: (Recipe) -> Unit) {
        onItemClickListener = listener

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if(constraint.isNullOrEmpty()){
                    filterResults.values = recipes
                    filterResults.count = recipes.size
                }else{
                    val filteredList = recipes.filter{
                        it.name?.contains(constraint, ignoreCase = true)!!
                    }
                    filterResults.values = filteredList
                    filterResults.count = filteredList.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                filteredRecipes = results.values as List<Recipe>
                notifyDataSetChanged()
            }
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Custom adapter, for the ListView inside the Recipe Card
class IngredientAdapter( context: Context, ingredients: List<Ingredient>): ArrayAdapter<Ingredient>(context,0,ingredients){

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View =convertView?: LayoutInflater.from(context).inflate(R.layout.ingredient_list_view,parent,false)

        val ingredient = getItem(position)

        val ingredientIcon: ImageView = view.findViewById(R.id.ingredient_icon)
        val ingredientName: TextView = view.findViewById(R.id.ingredient_name)
        val ingredientCount: TextView = view.findViewById(R.id.ingredient_count)

        if (ingredient != null)
        {
            Picasso.get()
                .load(ingredient.icon)
                .fit()
                .into(ingredientIcon)
        }

        ingredientName.text = ingredient?.name
        ingredientCount.text = "Count: ${ingredient?.count.toString()}"

        return view
    }
}



class CraftingFragment : Fragment(), RecipeTypeAdapter.OnItemClickListener {
    private var _binding: FragmentCraftingBinding? = null

    private var recipeList: MutableList<Recipe> = mutableListOf()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //val rootView = inflater.inflate(R.layout.fragment_crafting, container, false)

        val loader = LoaderDialogFragment.newInstance("Recipe Types..")
        loader.isCancelable = false
        loader.show(childFragmentManager, "Recipe Types")

        _binding = inflate(inflater, container, false)
        val root: View = binding.root

        val recipeTypes = resources.getStringArray(R.array.recipe_types)

        val recyclerView: RecyclerView = root.findViewById(R.id.recipe_recycler)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val dividerItemDecoration = DividerItemDecoration(context,LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.divider_white)!!)
        recyclerView.addItemDecoration(dividerItemDecoration)

        val adapter = RecipeTypeAdapter(recipeTypes,this)
        recyclerView.adapter = adapter
        loader.dismiss()

        return root
    }

    override fun onItemClick(recipe: String) {
        Toast.makeText(context, "Recipe Type: $recipe", Toast.LENGTH_SHORT).show()

        val loader = LoaderDialogFragment.newInstance("Recipes...")
        loader.isCancelable = false
        loader.show(childFragmentManager, "Recipes...")


        val inflater = requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_equipment_selection, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.animationStyle = android.R.style.Animation_Toast

        // Use RecyclerView instead of ListView
        val recyclerView: RecyclerView = popupView.findViewById(R.id.equipment_recycler_list)
        val searchView: SearchView? = popupView.findViewById<SearchView>(R.id.search_view)

        recyclerView.setRecycledViewPool(RecyclerView.RecycledViewPool())
        val adapter = RecipeAdapter(recipeList)
        recyclerView.adapter = adapter


        recyclerView.layoutManager = LinearLayoutManager(this@CraftingFragment.activity)
        val dividerItemDecoration = DividerItemDecoration(context,LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        searchView?.queryHint = "Search for recipe...."

        fetchRecipes(recipeList,recipe){ success ->
            if (success){
                adapter.notifyDataSetChanged()
                loader.dismiss()
                println("Successfully fetched recipes $recipe")
            }else{
                println("Failed to fetch recipes $recipe")
            }

        }

        adapter.setOnRecipeClickListener { selectedRecipe ->
            Toast.makeText(requireContext(),"Selected Recipe: ${selectedRecipe.name}", Toast.LENGTH_SHORT).show()
            showRecipeView(requireContext(), selectedRecipe)
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

        popupWindow.showAtLocation(view?.findViewById(R.id.fragment_crafting), Gravity.CENTER, 0, 0)
    }

    private fun fetchRecipes(
        itemList: MutableList<Recipe>, // The list to add items to
        collectionKey: String,      // The Firestore collection name
        onComplete: (Boolean) -> Unit // Callback to notify when the task is completed
    ) {
        // Get a reference to the Firestore instance
        val db = FirebaseFirestore.getInstance()

        // Check if the list is empty, reduces multiples of data
        if (itemList.isNotEmpty()){
            itemList.clear()
            Log.d(TAG, " is not empty.")
        }

        val collectionName = recipeMap[collectionKey]

        // Query the collection based on the collection name passed
        if (collectionName != null) {
            db.collection(collectionName).orderBy("name", Query.Direction.ASCENDING).limit(50)
                .get()
                .addOnSuccessListener { documents ->
                    // Loop through each document in the query result
                    for (document in documents) {
                        val recipe = document.toObject(Recipe::class.java)
                        itemList.add(recipe)
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
    }

    private fun showRecipeView(context: Context, recipe: Recipe) {
        // Inflate the recipe_view layout
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.recipe_view, null)

        val recipeIcon: ImageView = view.findViewById(R.id.item_icon)
        val recipeName: TextView = view.findViewById(R.id.recipe_name)

        loadImageWithPicasso(recipeIcon,recipe.icon)
        recipeName.text = recipe.name

        // Get a reference to the ListView inside the recipe_view layout
        val listView: ListView = view.findViewById(R.id.ingredients_list)




        // Set up an ArrayAdapter to display the ingredients in the ListView
        val adapter = IngredientAdapter(requireContext(),recipe.ingredients)
        listView.adapter = adapter


        // Create an AlertDialog to show the recipe view
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        // Show the dialog
        dialog.show()

        // Set an OnClickListener on the root view to dismiss the dialog when clicked
        view.setOnClickListener {
            dialog.dismiss()  // Close the view when clicked
        }
    }

    private fun loadImageWithPicasso(imageView: ImageView, url: String) {

        Picasso.get()
            .load(url)
            .fit()
            .into(imageView)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}