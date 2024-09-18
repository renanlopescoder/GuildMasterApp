package com.ai.guildmasterapp.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ai.guildmasterapp.R

class OverviewFragment : Fragment(R.layout.fragment_dashboard_overview) {

    //Initialize variables
    private lateinit var profileCard : CardView
    private lateinit var compareEquipmentCard : CardView
    private lateinit var compareWeaponsCard : CardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Assign Variables to their respective ID
        profileCard = view.findViewById(R.id.profile_card)
        compareEquipmentCard = view.findViewById(R.id.compare_equipment_card)
        compareWeaponsCard = view.findViewById(R.id.compare_weapons_card)


        //Set Cards to respond when being clicked on
        setCardVisualListeners(profileCard)
        setCardVisualListeners(compareEquipmentCard)
        setCardVisualListeners(compareWeaponsCard)

        compareEquipmentCard.setOnClickListener {
            val navigationIntent = Intent(requireContext(),CompareEquipment::class.java)
            startActivity(navigationIntent)
        }

        profileCard.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile)
        }

    }

//    @SuppressLint("ClickableViewAccessibility")
    private fun setCardVisualListeners(cardView1: CardView)
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