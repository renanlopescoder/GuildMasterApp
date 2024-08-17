package com.ai.guildmasterapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.ai.guildmasterapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawer_layout) // Initializes drawerLayout

       val bottomNavView: BottomNavigationView = binding.navView // Navigation for bottom menu items

        val navView: NavigationView = binding.navViewDrawerMenu // Navigation for drawer menu items

        // Host fragment to navigate between screens
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
                R.id.navigation_messages, R.id.navigation_menu, R.id.navigation_news, R.id.navigation_pvp_history,
                R.id.navigation_guild, R.id.navigation_community, R.id.navigation_crafting, R.id.navigation_my_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNavView.setupWithNavController(navController)

        // Handle bottom nav item clicks
        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_menu -> {
                    drawerLayout.openDrawer(GravityCompat.START)  // Opens side menu
                    true
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(item, navController)
                    true
                }
            }
        }

        navView.findViewById<Button>(R.id.button_about).setOnClickListener {

            // Implement code to handle about button click
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
