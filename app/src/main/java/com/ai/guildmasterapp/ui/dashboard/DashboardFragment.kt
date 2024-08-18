package com.ai.guildmasterapp.ui.dashboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ai.guildmasterapp.R
import android.widget.PopupWindow
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
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
            ViewModelProvider(this)[DashboardViewModel::class.java]

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
        filterButton = root.findViewById(R.id.filter_button)

        //THESE FUNCTION CALLS ARE FOR TESTING ONLY
        setDashboardGreeting("Warrior")
        setLearningCount(30)
        setCraftingQuestsCount(14)
        setCompletedQuestsCount(100)

        //THESE FUNCTION CALLS CHANGE THE ELEVATION AND COLOR OF THE 3 CARDS AT THE BOTTOM
        setCardListeners(craftingQuestsCard)
        setCardListeners(overallQuestsCard)
        setCardListeners(learningCard)


        //THIS BINDS THE TOGGLE BUTTONS SO OVERVIEW IS ON WHEN PVP IS OFF, AND VISA VERSA
        bindToggleButtons(overviewToggleButton,pvpToggleButton)

        //This brings up the popup window
        filterButton.setOnClickListener{
            showFilters(it)
        }






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

    @SuppressLint("UseCompatOrMaterialCode")
    private fun showFilters(view: View){
        val rootLayout: ConstraintLayout = requireView().findViewById(R.id.root_layout)
        val overlay: View = requireView().findViewById(R.id.overlay)

        val inflater = layoutInflater
        val popupView = inflater.inflate(R.layout.popop_overview_filter,null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,)

        overlay.visibility = View.VISIBLE

        //Open the popupWindow at the bottom of the screen
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0)


        //Access the switches
        val craftingQuestsSwitch: Switch = popupView.findViewById(R.id.switchQuest)
        val completedQuestsSwitch: Switch = popupView.findViewById(R.id.switchQuestsCompleted)
        val learningSwitch: Switch = popupView.findViewById(R.id.switchLearning)
        val saveButton: Button = popupView.findViewById(R.id.save_changes_button)
        val clearAllButton: Button = popupView.findViewById(R.id.clear_all_button)

        // Set the switches' states based on the current visibility of the CardViews
        craftingQuestsSwitch.isChecked = craftingQuestsCard.visibility == View.VISIBLE
        completedQuestsSwitch.isChecked = overallQuestsCard.visibility == View.VISIBLE
        learningSwitch.isChecked = learningCard.visibility == View.VISIBLE

        // Variables to store the desired visibility state
        var craftingSwitchState = craftingQuestsSwitch.isChecked
        var overallQuestsSwitchState = completedQuestsSwitch.isChecked
        var learningSwitchState = learningSwitch.isChecked

        //Listeners to change the state based on the switch
        craftingQuestsSwitch.setOnCheckedChangeListener { _, isChecked ->
            craftingSwitchState = isChecked
        }

        completedQuestsSwitch.setOnCheckedChangeListener { _, isChecked ->
            overallQuestsSwitchState = isChecked
        }

        learningSwitch.setOnCheckedChangeListener { _, isChecked ->
            learningSwitchState = isChecked
        }

        //Save the changes if the button is pressed, and close the pop-up window
        saveButton.setOnClickListener{
            craftingQuestsCard.visibility = if (craftingSwitchState) View.VISIBLE else View.GONE
            overallQuestsCard.visibility = if (overallQuestsSwitchState) View.VISIBLE else View.GONE
            learningCard.visibility = if (learningSwitchState) View.VISIBLE else View.GONE

            popupWindow.dismiss()
        }


        //Turn the switches off and change the state
        clearAllButton.setOnClickListener{
            craftingQuestsSwitch.isChecked = false
            completedQuestsSwitch.isChecked = false
            learningSwitch.isChecked = false

            craftingSwitchState = false
            overallQuestsSwitchState = false
            learningSwitchState = false
        }

        popupWindow.setOnDismissListener {
            overlay.visibility = View.GONE
        }

        //Close the window if touched anywhere else, does not save any changes
        popupView.setOnTouchListener {_,_->
            popupWindow.dismiss()
            true
        }
    }



}


