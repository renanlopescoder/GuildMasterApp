package com.ai.guildmasterapp.ui.dashboard

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Context
import android.widget.PopupWindow
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ai.guildmasterapp.R
import com.ai.guildmasterapp.databinding.FragmentDashboardBinding



class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var dashboardGreeting: TextView
    private lateinit var profileImage: ImageView
    private lateinit var craftingQuestsCard: CardView
    private lateinit var overallQuestsCard: CardView
    private lateinit var learningCard: CardView
    private lateinit var craftingQuestsCount: TextView
    private lateinit var completedQuestsCount: TextView
    private lateinit var learningCount: TextView
    private lateinit var overviewToggleButton: ToggleButton
    private lateinit var pvpToggleButton: ToggleButton
    private lateinit var filterButton: ImageButton


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dashboardGreeting = root.findViewById(R.id.dashboard_greeting)
        profileImage = root.findViewById(R.id.dashboard_temp_prof_pic)
        craftingQuestsCard = root.findViewById(R.id.crafting_quests_card)
        overallQuestsCard = root.findViewById(R.id.overall_quests_card)
        learningCard = root.findViewById(R.id.learning_card)

        craftingQuestsCount = root.findViewById(R.id.crafting_quests_count)
        completedQuestsCount = root.findViewById(R.id.completed_quests_count)
        learningCount = root.findViewById(R.id.learning_count)
        overviewToggleButton = root.findViewById(R.id.dashboard_overview_toggle)
        pvpToggleButton = root.findViewById(R.id.dashboard_pvp_toggle)

        //THESE FUNCTION CALLS ARE FOR TESTING ONLY
        setDashboardGreeting("Swordsman")
        setLearningCount(20)
        setCraftingQuestsCount(200)
        setCompletedQuestsCount(2000)

        //THESE FUNCTION CALLS CHANGE THE ELEVATION AND COLOR OF THE 3 CARDS AT THE BOTTOM
        setCardListeners(craftingQuestsCard)
        setCardListeners(overallQuestsCard)
        setCardListeners(learningCard)

        //THIS BINDS THE TOGGLE BUTTONS SO OVERVIEW IS ON WHEN PVP IS OFF, AND VISA VERSA
        bindToggleButtons(overviewToggleButton,pvpToggleButton)








        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setDashboardGreeting(usersClass : String)
    {
        "Hello, \n$usersClass !".also { dashboardGreeting.text = it }
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
            pvpToggleButton.isChecked = !isChecked
        }
        pvpToggleButton.setOnCheckedChangeListener { _, isChecked ->
            overviewToggleButton.isChecked = !isChecked
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setCardListeners(cardView1: CardView)
    {
        cardView1.setOnTouchListener { _, event ->
            when (event.action)
            {
                MotionEvent.ACTION_DOWN ->
                    {
                    cardView1.elevation = 0f // Decrease elevation
                    val yellowClear = Color.parseColor("#99F4E842")
                    cardView1.setCardBackgroundColor(yellowClear)
                    }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    {
                    cardView1.elevation = 8f // Default elevation
                    val defaultColor = Color.parseColor("#CCFFFFFF")
                    cardView1.setCardBackgroundColor(defaultColor)
                    cardView1.performClick()
                    }
            }
            false
        }
    }

    private fun showPopupWindow(view: View) {
        // Inflate the popup_window.xml
        val inflater: LayoutInflater = getSystemService(this,Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_window, null)

        // Create the PopupWindow
        val popupWindow = PopupWindow(popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true)

        // Show the PopupWindow
        popupWindow.showAsDropDown(view, 0, 0)

        // Set up click listeners for the buttons
        val clearAllButton: Button = popupView.findViewById(R.id.clearAllButton)
        val saveChangesButton: Button = popupView.findViewById(R.id.saveChangesButton)

        clearAllButton.setOnClickListener {
            // Handle Clear All button click
            popupWindow.dismiss()
        }

        saveChangesButton.setOnClickListener {
            // Handle Save Changes button click
            popupWindow.dismiss()
        }
    }


}


