package com.ai.guildmasterapp

import android.os.Bundle
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
                R.id.navigation_messages, R.id.navigation_menu
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
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
