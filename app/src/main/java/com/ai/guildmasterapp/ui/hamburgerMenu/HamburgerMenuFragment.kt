package com.ai.guildmasterapp.ui.hamburgerMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ai.guildmasterapp.R
import com.ai.guildmasterapp.databinding.FragmentHamburgerBinding
import com.google.android.material.navigation.NavigationView

class HamburgerMenuFragment : Fragment() {

    private var _drawerLayout: DrawerLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_main, container, false)


        val navView: NavigationView = view.findViewById(R.id.nav_view_drawer_menu)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    // Handle Item 1 selection
                    Toast.makeText(context, "Item 1 selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_messages -> {
                    // Handle Item 2 selection
                    Toast.makeText(context, "Item 2 selected", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // _drawerLayout = null
    }
}