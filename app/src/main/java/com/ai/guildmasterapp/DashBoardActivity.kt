package com.ai.guildmasterapp

import android.media.Image
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashBoardActivity : AppCompatActivity()
{
    private lateinit var dashboardGreeting : TextView
    private lateinit var profileImage: ImageView


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        dashboardGreeting = findViewById(R.id.dashboard_greeting)
        profileImage = findViewById(R.id.dashboard_prof_pic)
    }



    fun setDashboardGreeting(usersClass : String)
    {
        "Hello, \n $usersClass !".also { dashboardGreeting.text = it }
    }

    fun setProfileImage(usersProfilePic: Int)
    {
        profileImage.setImageResource(usersProfilePic)
    }









}