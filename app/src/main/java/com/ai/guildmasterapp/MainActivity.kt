package com.ai.guildmasterapp


import android.os.Bundle

import android.content.Intent

import android.widget.Button
import android.widget.TextView

import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.ai.guildmasterapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.ai.guildmasterapp.api.GuildWars2Api
import kotlinx.coroutines.launch
import android.widget.*

import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var api = GuildWars2Api()
    private var globalState = GlobalState
    private lateinit var username: TextView
    private lateinit var userEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide() // Removes the action bar from the view.

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        drawerLayout = findViewById(R.id.drawer_layout) // Initializes drawerLayout

        // Begins coroutine to fetch all the item IDs
        lifecycleScope.launch {
            // fetches a list of almost every item ID in the game and stores it in the global state
            getItemIds()
        }

       val bottomNavView: BottomNavigationView = binding.navViewBottomMenu // Navigation for bottom menu items

        val navView: NavigationView = binding.navViewDrawerMenu // Navigation for drawer menu items

        // Host fragment to navigate between screens
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_friends, R.id.navigation_events,
                R.id.navigation_messages, R.id.navigation_menu, R.id.navigation_pvp_history,
                R.id.navigation_guild, R.id.navigation_community, R.id.navigation_crafting, R.id.navigation_profile,
                R.id.navigation_about
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNavView.setupWithNavController(navController)

        val headerView = navView.getHeaderView(0)

        username = headerView.findViewById(R.id.nav_header_name)
        userEmail = headerView.findViewById(R.id.nav_header_race)

        username.text = GlobalState.characterDetail?.name ?: "Guest"
        userEmail.text = FirebaseAuth.getInstance().currentUser?.email ?: "guest@example.com"

        // Handles bottom menu item clicks, navigates to the appropriate page
        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_menu -> {
                    drawerLayout.openDrawer(GravityCompat.START)  // Opens side menu

                    // Initializes variables for the textViews in the nav_drawer_header_main.xml
                    val headerNameView = navView.findViewById<TextView>(R.id.nav_header_name)
                    val headerRaceView = navView.findViewById<TextView>(R.id.nav_header_race)

                    // Stores the name & race of the selected character
                    val characterName = globalState.characterDetail?.name
                    val characterRace = globalState.characterDetail?.race

                    // Sets the text for the TextViews
                    headerNameView.text = characterName
                    headerRaceView.text = characterRace
                        true
                }
                R.id.navigation_dashboard -> {
                    navController.navigate(R.id.navigation_dashboard)
                    true
                }
                R.id.navigation_friends -> {
                    navController.navigate(R.id.navigation_friends)
                    true
                }
                R.id.navigation_events -> {
                    navController.navigate(R.id.navigation_events)
                    true
                }
                R.id.navigation_messages -> {
                    navController.navigate(R.id.navigation_messages)
                    true
                }
                R.id.navigation_profile -> {
                    navController.navigate(R.id.navigation_profile)
                    true
                }
                else -> false
            }

        }


        // When a drawer menu item is selected, it navigates to the appropriate page and closes the menu
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    navController.navigate(R.id.navigation_dashboard)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.navigation_friends -> {
                    navController.navigate(R.id.navigation_friends)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.navigation_messages -> {
                    navController.navigate(R.id.navigation_messages)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.navigation_pvp_history -> {
                    navController.navigate(R.id.navigation_pvp_history)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.navigation_pvp_history -> {
                    navController.navigate(R.id.navigation_pvp_history)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.navigation_guild -> {
                    navController.navigate(R.id.navigation_guild)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.navigation_community -> {
                    navController.navigate(R.id.navigation_community)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.navigation_crafting -> {
                    navController.navigate(R.id.navigation_crafting)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.navigation_profile -> {
                    navController.navigate(R.id.navigation_profile)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }

        // When the about button is clicked
        navView.findViewById<Button>(R.id.button_about).setOnClickListener {

            navController.navigate(R.id.navigation_about) // Navigates to about page.
            drawerLayout.closeDrawer(GravityCompat.START) // Closes side menu

        }

        // When the log-out button is clicked
        navView.findViewById<Button>(R.id.button_log_out).setOnClickListener {

            // Initializes intent to navigate back to the login screen
            val intent = Intent(this, LoginActivity::class.java)

            // Ensures that the user cannot go back to the main activity
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(intent) // Opens the Login screen
            finish() // Ends MainActivity
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Stores a list of ints for every item ID in the game in the global state.
    private suspend fun getItemIds() {

        api.fetchItemIds()

    }


}
