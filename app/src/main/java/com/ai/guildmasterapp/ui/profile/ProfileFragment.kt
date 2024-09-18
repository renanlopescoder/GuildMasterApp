package com.ai.guildmasterapp.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ai.guildmasterapp.api.GuildWars2Api
import com.ai.guildmasterapp.databinding.FragmentProfileBinding
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.ItemType
import com.ai.guildmasterapp.LoaderDialogFragment
import com.ai.guildmasterapp.R
import com.ai.guildmasterapp.ui.dashboard.CompareEquipment
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    private val api = GuildWars2Api() // Initializes variable for the api

    private val powerModifiers = ArrayList<Int>()
    private val precisionModifiers = ArrayList<Int>()
    private val toughnessModifiers = ArrayList<Int>()
    private val vitalityModifiers = ArrayList<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using FragmentMyProfileBinding
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val characterBackstory = GlobalState.characterDetail?.backstory // Initializes backstory instance

        val characterEquipment = GlobalState.characterDetail?.equipment

        val items = ArrayList<ItemType>()



        // Sets the question & answer textViews to show a portion of the backstory
        lifecycleScope.launch {
            setBackstory(characterBackstory)

            characterEquipment?.forEach { equipment ->

                if(equipment.id != 97730) {
                    val itemType = api.fetchItemDetails(equipment.id)
                    items.add(itemType!!)
                }
            }

            items.forEach { item ->
                when(item.type) {
                    "Weapon" -> {
                        val weaponAttributes = GlobalState.weaponMap[item.id]?.details?.infix_upgrade?.attributes

                        weaponAttributes?.forEach { weapon ->
                            when(weapon.attribute) {
                                "Power" -> {
                                    powerModifiers.add(weapon.modifier)
                                }

                                "Precision" -> {
                                    precisionModifiers.add(weapon.modifier)
                                }

                                "Toughness" -> {
                                    toughnessModifiers.add(weapon.modifier)
                                }

                                "Vitality" -> {
                                    vitalityModifiers.add(weapon.modifier)
                                }

                            }
                        }
                    }

                    "Armor" -> {
                        val armorAttributes = GlobalState.armorMap[item.id]?.details?.infix_upgrade?.attributes

                        armorAttributes?.forEach { armor ->
                            when(armor.attribute) {
                                "Power" -> {
                                    powerModifiers.add(armor.modifier)
                                }

                                "Precision" -> {
                                    precisionModifiers.add(armor.modifier)
                                }

                                "Toughness" -> {
                                    toughnessModifiers.add(armor.modifier)
                                }

                                "Vitality" -> {
                                    vitalityModifiers.add(armor.modifier)
                                }

                            }
                        }
                    }

                }

            }

            displayAttributes() // Displays the character's base stats

        }


        // Displays backstory dialog window when the question or answer textView is clicked.
        binding.profileQuestion1.setOnClickListener {
            lifecycleScope.launch {

                displayBackstory(characterBackstory?.get(0)!!)
            }
        }

        binding.profileAnswer1.setOnClickListener {
            lifecycleScope.launch {

                displayBackstory(characterBackstory?.get(0)!!)
            }

        }

        binding.profileQuestion2.setOnClickListener {
            lifecycleScope.launch {

                displayBackstory(characterBackstory?.get(1)!!)
            }
        }

        binding.profileAnswer2.setOnClickListener {
            lifecycleScope.launch {

                displayBackstory(characterBackstory?.get(1)!!)
            }

        }

        binding.profileQuestion3.setOnClickListener {
            lifecycleScope.launch {

                displayBackstory(characterBackstory?.get(2)!!)
            }
        }

        binding.profileAnswer3.setOnClickListener {
            lifecycleScope.launch {

                displayBackstory(characterBackstory?.get(2)!!)
            }

        }

        binding.profileQuestion4.setOnClickListener {
            lifecycleScope.launch {

                displayBackstory(characterBackstory?.get(3)!!)
            }
        }

        binding.profileAnswer4.setOnClickListener {
            lifecycleScope.launch {

                displayBackstory(characterBackstory?.get(3)!!)
            }

        }

        binding.profileQuestion5.setOnClickListener {
            lifecycleScope.launch {

                displayBackstory(characterBackstory?.get(4)!!)
            }
        }

        binding.profileAnswer5.setOnClickListener {
            lifecycleScope.launch {

                displayBackstory(characterBackstory?.get(4)!!)
            }

        }


        // Starts the CompareEquipment Activity when the equipment calculator button is clicked
        binding.profileEquipmentButton.setOnClickListener {

            val intent = Intent(requireContext(),CompareEquipment::class.java)

            startActivity(intent)
        }

        // Navigates to the guild fragment when the guild button is clicked
        binding.profileGuildButton.setOnClickListener {

            findNavController().navigate(R.id.navigation_guild)
        }


        displayCharacterDetails() // Displays the character's name, race, profession, and level

        return binding.root
    }


    private suspend fun setBackstory(backstories: List<String?>?) {

        val loader = LoaderDialogFragment.newInstance("Preparing Profile") // Initializes Loading dialog
        loader.isCancelable = false
        loader.show(childFragmentManager,"loader") // Displays dialog


        // Stores the answer for each character backstory
        val answer1 = api.fetchBackstoryAnswer(backstories?.get(0)!!)
        val answer2 = api.fetchBackstoryAnswer(backstories[1]!!)
        val answer3 = api.fetchBackstoryAnswer(backstories[2]!!)
        val answer4 = api.fetchBackstoryAnswer(backstories[3]!!)
        val answer5 = api.fetchBackstoryAnswer(backstories[4]!!)

        // Stores the question for each character backstory
        val question1 = api.fetchBackstoryQuestions(answer1!!.question)
        val question2 = api.fetchBackstoryQuestions(answer2!!.question)
        val question3 = api.fetchBackstoryQuestions(answer3!!.question)
        val question4 = api.fetchBackstoryQuestions(answer4!!.question)
        val question5 = api.fetchBackstoryQuestions(answer5!!.question)


        // Sets the text for the profileAnswer textViews
        binding.profileAnswer1.text = answer1.description.substring(0, 18) + "..."
        binding.profileAnswer2.text = answer2.description.substring(0, 26) + "..."
        binding.profileAnswer3.text = answer3.description.substring(0, 20) + "..."
        binding.profileAnswer4.text = answer4.description.substring(0, 21) + "..."
        binding.profileAnswer5.text = answer5.description.substring(0, 20) + "..."

        // Sets the text for the profileQuestion textViews
        binding.profileQuestion1.text = question1?.description?.substring(0, 18) + "..."
        binding.profileQuestion2.text = question2?.description?.substring(0, 32) + "..."
        binding.profileQuestion3.text = question3?.description?.substring(0, 22) + "..."
        binding.profileQuestion4.text = question4?.description?.substring(0, 25) + "..."
        binding.profileQuestion5.text = question5?.description?.substring(0, 28) + "..."

        loader.dismiss() // Closes loading dialog after the data is fetched
    }


    private suspend fun displayBackstory(backstory: String?) {

        // Initializes backstory answer & question objects
        val backstoryAnswers = api.fetchBackstoryAnswer(backstory!!)
        val backstoryQuestion = api.fetchBackstoryQuestions(backstoryAnswers!!.question)

        // Shows the full backstory question & answer in a dialog window
        showDialogBackstory(backstoryAnswers.description, backstoryQuestion!!.description)
    }



    private fun showDialogBackstory(answer: String, question: String) {

        // Initializes a dialog window
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_backstory, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val dialog = dialogBuilder.create()

        // Creates views & button for the dialog window
        val backstoryQuestion = dialogView.findViewById<TextView>(R.id.profile_dialog_question)
        val backstoryAnswer = dialogView.findViewById<TextView>(R.id.profile_dialog_answer)
        val closeButton = dialogView.findViewById<Button>(R.id.backstory_close_button)

        // Set the player's backstory text
        backstoryAnswer.text = answer
        backstoryQuestion.text = question

        // Close the dialog when the button is clicked
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()

    }


    private fun displayCharacterDetails() {
        binding.profileName.text = GlobalState.characterDetail?.name
        binding.profileRace.text = GlobalState.characterDetail?.race
        binding.profileLevel.text = GlobalState.characterDetail?.level.toString()
        binding.profileProfession.text = GlobalState.characterDetail?.profession
    }


    private fun displayAttributes() {
        val playerLevel : Int = GlobalState.characterDetail!!.level
        val baseStat = (playerLevel.toFloat() / 80) * 1000

        var powerStat = baseStat.toInt()
        var precisionStat = baseStat.toInt()
        var toughnessStat = baseStat.toInt()
        var vitalityStat = baseStat.toInt()

        powerModifiers.forEach { modifier ->
            powerStat += modifier
        }
        precisionModifiers.forEach { modifier ->
            precisionStat += modifier
        }
        toughnessModifiers.forEach { modifier ->
            toughnessStat += modifier
        }
        vitalityModifiers.forEach { modifier ->
            vitalityStat += modifier
        }

        binding.profileAttributePowerStat.text = powerStat.toString()
        binding.profileAttributePrecisionStat.text = precisionStat.toString()
        binding.profileAttributeToughnessStat.text = toughnessStat.toString()
        binding.profileAttributeVitalityStat.text = vitalityStat.toString()

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}