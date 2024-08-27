package com.ai.guildmasterapp

import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity


class DashBoard_Activity : FragmentActivity()
{
    private lateinit var dashboardGreeting : TextView
    private lateinit var profileImage: ImageView
    private lateinit var overviewToggleButton: ToggleButton
    private lateinit var pvpToggleButton: ToggleButton
    private lateinit var filterButton: ImageButton


    @SuppressLint("MissingInflatedId")

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val view = inflater.inflate(R.layout.fragment_dashboard,container, false)

        dashboardGreeting = findViewById(R.id.dashboard_greeting)
        profileImage = findViewById(R.id.dashboard_temp_prof_pic)
        overviewToggleButton = findViewById(R.id.dashboard_overview_toggle)
        pvpToggleButton = findViewById(R.id.dashboard_pvp_toggle)
        filterButton = findViewById(R.id.filter_button)

        val user = GlobalState
        val userProfession = user.characterDetail?.profession
        Log.d("Users Profession", userProfession.toString())
        if (userProfession != null) {
            setDashboardGreeting(userProfession)
        }


        bindToggleButtons(overviewToggleButton,pvpToggleButton)
        return view
    }


    private fun setDashboardGreeting(usersClass : String)
    {
        "Hello, \n $usersClass !".also { dashboardGreeting.text = it }
    }

    private fun setProfileImage(usersProfilePic: Int)
    {
        profileImage.setImageResource(usersProfilePic)
    }





    private fun bindToggleButtons(overviewToggleButton: ToggleButton, pvpToggleButton: ToggleButton)
    {
        overviewToggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                pvpToggleButton.isChecked = false
            }
        }
        pvpToggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                overviewToggleButton.isChecked = false
            }
        }
    }










}