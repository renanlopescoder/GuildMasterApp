package com.ai.guildmasterapp


import android.app.Notification.Action
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.ActionBar

import android.content.Intent

import android.widget.Button

import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.ai.guildmasterapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.ai.guildmasterapp.GlobalState


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide() // Removes the action bar from the view.

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        drawerLayout = findViewById(R.id.drawer_layout) // Initializes drawerLayout



       val bottomNavView: BottomNavigationView = binding.navViewBottomMenu // Navigation for bottom menu items


        val navView: NavigationView = binding.navViewDrawerMenu // Navigation for drawer menu items

        // Host fragment to navigate between screens
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
                R.id.navigation_messages, R.id.navigation_menu, R.id.navigation_news, R.id.navigation_pvp_history,
                R.id.navigation_guild, R.id.navigation_community, R.id.navigation_crafting, R.id.navigation_my_profile,
                R.id.navigation_about
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNavView.setupWithNavController(navController)

        // Handles bottom menu item clicks, navigates to the appropriate page
        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_menu -> {
                    drawerLayout.openDrawer(GravityCompat.START)  // Opens side menu
                    true
                }
                R.id.navigation_dashboard -> {
                    navController.navigate(R.id.navigation_dashboard)
                    true
                }
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_notifications -> {
                    navController.navigate(R.id.navigation_notifications)
                    true
                }
                R.id.navigation_messages -> {
                    navController.navigate(R.id.navigation_messages)
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
                R.id.navigation_messages -> {
                    navController.navigate(R.id.navigation_messages)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.navigation_news -> {
                    navController.navigate(R.id.navigation_news)
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
                R.id.navigation_friends -> {
                    navController.navigate(R.id.navigation_friends)
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
                R.id.navigation_my_profile -> {
                    navController.navigate(R.id.navigation_my_profile)
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
}
