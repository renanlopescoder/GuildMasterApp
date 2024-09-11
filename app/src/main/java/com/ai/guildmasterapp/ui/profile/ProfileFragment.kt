package com.ai.guildmasterapp.ui.profile

import android.annotation.SuppressLint
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
import com.ai.guildmasterapp.databinding.FragmentMyProfileBinding
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.R
import com.ai.guildmasterapp.ui.dashboard.CompareEquipment
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {
    private var _binding: FragmentMyProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    private val api = GuildWars2Api() // Initializes variable for the api

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using FragmentMyProfileBinding
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        val characterBackstory = GlobalState.characterDetail?.backstory // Initializes backstory instance


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


        // Starts the CompareEquipment Activity
        binding.profileEquipmentButton.setOnClickListener {

            val intent = Intent(requireContext(),CompareEquipment::class.java)

            startActivity(intent)
        }

        // Navigates to the guild fragment
        binding.profileGuildButton.setOnClickListener {

            findNavController().navigate(R.id.navigation_guild)
        }


        displayCharacterDetails() // Displays the character's name, race, profession, and level
        displayAttributes() // Displays the character's base stats

        return binding.root
    }


    private suspend fun displayBackstory(backstory: String?) {

        val backstoryAnswers = api.fetchBackstoryAnswer(backstory!!)
        val backstoryQuestion = api.fetchBackstoryQuestions(backstoryAnswers!!.question)
        showDialogBackstory(backstoryAnswers.description, backstoryQuestion!!.description)
    }



    private fun showDialogBackstory(answer: String, question: String) {

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_backstory, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val dialog = dialogBuilder.create()

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

        binding.profileAttributePowerStat.text = baseStat.toInt().toString()
        binding.profileAttributePrecisionStat.text = baseStat.toInt().toString()
        binding.profileAttributeToughnessStat.text = baseStat.toInt().toString()
        binding.profileAttributeVitalityStat.text = baseStat.toInt().toString()

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}