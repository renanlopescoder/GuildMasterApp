package com.ai.guildmasterapp.ui.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ai.guildmasterapp.api.GuildWars2Api
import com.ai.guildmasterapp.databinding.FragmentMyProfileBinding
import coil.load
import com.ai.guildmasterapp.Equipment
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.ItemType
import com.ai.guildmasterapp.R
import com.ai.guildmasterapp.R.id.profile_answer_1
import kotlinx.coroutines.GlobalScope
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

        binding.profileQuestion1.setOnClickListener {
            lifecycleScope.launch {

                val characterBackstory = GlobalState.characterDetail?.backstory

                val backstory = api.fetchBackstoryAnswer(characterBackstory?.get(0)!!)
                displayBackstory(backstory!!.description)
            }
        }

        binding.profileAnswer1.setOnClickListener {
            lifecycleScope.launch {

                val characterBackstory = GlobalState.characterDetail?.backstory

                val backstory = api.fetchBackstoryAnswer(characterBackstory?.get(0)!!)
                displayBackstory(backstory!!.description)
            }

        }

        displayCharacterDetails()

        displayAttributes()

        return binding.root
    }



    @SuppressLint("MissingInflatedId")
    private fun displayBackstory(backstory: String) {


        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_backstory, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val dialog = dialogBuilder.create()

        val backstoryTextView = dialogView.findViewById<TextView>(R.id.profile_answer_1)
        val closeButton = dialogView.findViewById<Button>(R.id.backstory_close_button)

        // Set the player's backstory text
        backstoryTextView.text = backstory

        // Close the dialog when the button is clicked
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
        /*val characterBackstory = GlobalState.characterDetail?.backstory


        val backstory = api.fetchBackstoryAnswer(characterBackstory?.get(0)!!)

        binding.profileAnswer1.text = backstory?.description

        characterBackstory?.forEach {answer ->
            val backstory = api.fetchBackstoryAnswer(answer!!)


        }*/
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