package com.ai.guildmasterapp

import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
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


class DashBoardActivity : FragmentActivity()
{
    private lateinit var dashboardGreeting : TextView
    private lateinit var profileImage: ImageView
    private lateinit var craftingQuestsCard: CardView
    private lateinit var craftingQuestsCount: TextView
    private lateinit var completedQuestsCount: TextView
    private lateinit var learningCount: TextView
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
        craftingQuestsCard = findViewById(R.id.crafting_quests_card)
        craftingQuestsCount = findViewById(R.id.crafting_quests_count)
        completedQuestsCount = findViewById(R.id.completed_quests_count)
        learningCount = findViewById(R.id.learning_count)
        overviewToggleButton = findViewById(R.id.dashboard_overview_toggle)
        pvpToggleButton = findViewById(R.id.dashboard_pvp_toggle)
        filterButton = findViewById(R.id.filter_button)

        setDashboardGreeting("JESSE")
        setCraftingQuestsCount(20)
        setCompletedQuestsCount(5)
        setLearningCount(10)

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

    private fun setCraftingQuestsCount(usersCraftingQuests: Int)
    {
        craftingQuestsCount.text = usersCraftingQuests.toString()
    }

    private fun setCompletedQuestsCount(usersCompletedQuests: Int)
    {
        completedQuestsCount.text = usersCompletedQuests.toString()
    }

    private fun setLearningCount(usersLearningCount: Int)
    {
        learningCount.text = usersLearningCount.toString()
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