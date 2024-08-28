package com.ai.guildmasterapp.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.ai.guildmasterapp.R

class OverviewFragment : Fragment(R.layout.fragment_dashboard_overview) {

    //Initialize variables
    private lateinit var craftingQuestsCard : CardView
    private lateinit var overallQuestsCard : CardView
    private lateinit var learningCard : CardView
    private lateinit var craftingQuestsCount:TextView
    private lateinit var completedQuestsCount:TextView
    private lateinit var learningCount:TextView



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Assign Variables to their respective ID
        craftingQuestsCard = view.findViewById(R.id.crafting_quests_card)
        overallQuestsCard = view.findViewById(R.id.overall_quests_card)
        learningCard = view.findViewById(R.id.learning_card)
        craftingQuestsCount = view.findViewById(R.id.crafting_quests_count)
        completedQuestsCount = view.findViewById(R.id.completed_quests_count)
        learningCount = view.findViewById(R.id.learning_count)

        //Testing Only
        setCraftingQuestsCount(10)
        setCompletedQuestsCount(20)
        setLearningCount(30)

        //Set Cards to respond when being clicked on
        setCardListeners(craftingQuestsCard)
        setCardListeners(overallQuestsCard)
        setCardListeners(learningCard)

    }

    private fun setCraftingQuestsCount(usersCraftingQuests: Int) {
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


//    @SuppressLint("ClickableViewAccessibility")
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
//
//    @SuppressLint("UseCompatOrMaterialCode")
//    private fun showFilters(view: View){
//
//        val overlay: View = requireView().findViewById(R.id.overlay)
//
//        val inflater = layoutInflater
//        val popupView = inflater.inflate(R.layout.popop_overview_filter,null)
//
//        val popupWindow = PopupWindow(
//            popupView,
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT,)
//
//        overlay.visibility = View.VISIBLE
//
//        //Open the popupWindow at the bottom of the screen
//        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0)
//
//
//        //Access the switches
//        val craftingQuestsSwitch: Switch = popupView.findViewById(R.id.switchQuest)
//        val completedQuestsSwitch: Switch = popupView.findViewById(R.id.switchQuestsCompleted)
//        val learningSwitch: Switch = popupView.findViewById(R.id.switchLearning)
//        val saveButton: Button = popupView.findViewById(R.id.save_changes_button)
//        val clearAllButton: Button = popupView.findViewById(R.id.clear_all_button)
//
//        // Set the switches' states based on the current visibility of the CardViews
//        craftingQuestsSwitch.isChecked = craftingQuestsCard.visibility == View.VISIBLE
//        completedQuestsSwitch.isChecked = overallQuestsCard.visibility == View.VISIBLE
//        learningSwitch.isChecked = learningCard.visibility == View.VISIBLE
//
//        // Variables to store the desired visibility state
//        var craftingSwitchState = craftingQuestsSwitch.isChecked
//        var overallQuestsSwitchState = completedQuestsSwitch.isChecked
//        var learningSwitchState = learningSwitch.isChecked
//
//        //Listeners to change the state based on the switch
//        val onCheckedChangeListener = craftingQuestsSwitch.setOnCheckedChangeListener { _, isChecked ->
//            craftingSwitchState = isChecked
//        }
//
//        completedQuestsSwitch.setOnCheckedChangeListener { _, isChecked ->
//            overallQuestsSwitchState = isChecked
//        }
//
//        learningSwitch.setOnCheckedChangeListener { _, isChecked ->
//            learningSwitchState = isChecked
//        }
//
//        //Save the changes if the button is pressed, and close the pop-up window
//        saveButton.setOnClickListener{
//            craftingQuestsCard.visibility = if (craftingSwitchState) View.VISIBLE else View.GONE
//            overallQuestsCard.visibility = if (overallQuestsSwitchState) View.VISIBLE else View.GONE
//            learningCard.visibility = if (learningSwitchState) View.VISIBLE else View.GONE
//
//            popupWindow.dismiss()
//        }
//
//
//        //Turn the switches off and change the state
//        clearAllButton.setOnClickListener{
//            craftingQuestsSwitch.isChecked = false
//            completedQuestsSwitch.isChecked = false
//            learningSwitch.isChecked = false
//
//            craftingSwitchState = false
//            overallQuestsSwitchState = false
//            learningSwitchState = false
//        }
//
//        popupWindow.setOnDismissListener {
//            overlay.visibility = View.GONE
//        }
//
//        //Close the window if touched anywhere else, does not save any changes
//        popupView.setOnTouchListener {_,_->
//            popupWindow.dismiss()
//            true
//        }
//    }
//
//
//
//}
}