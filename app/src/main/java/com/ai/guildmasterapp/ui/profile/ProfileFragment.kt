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
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.R
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

                val backstoryAnswers = api.fetchBackstoryAnswer(characterBackstory?.get(0)!!)
                val backstoryQuestion = api.fetchBackstoryQuestions(backstoryAnswers!!.question)
                displayBackstory(backstoryAnswers.description, backstoryQuestion!!.description)
            }
        }

        binding.profileAnswer1.setOnClickListener {
            lifecycleScope.launch {

                val characterBackstory = GlobalState.characterDetail?.backstory

                val backstoryAnswers = api.fetchBackstoryAnswer(characterBackstory?.get(0)!!)
                val backstoryQuestion = api.fetchBackstoryQuestions(backstoryAnswers!!.question)
                displayBackstory(backstoryAnswers.description, backstoryQuestion!!.description)
            }

        }

        binding.profileQuestion2.setOnClickListener {
            lifecycleScope.launch {

                val characterBackstory = GlobalState.characterDetail?.backstory

                val backstoryAnswers = api.fetchBackstoryAnswer(characterBackstory?.get(1)!!)
                val backstoryQuestion = api.fetchBackstoryQuestions(backstoryAnswers!!.question)
                displayBackstory(backstoryAnswers.description, backstoryQuestion!!.description)
            }
        }

        binding.profileAnswer2.setOnClickListener {
            lifecycleScope.launch {

                val characterBackstory = GlobalState.characterDetail?.backstory

                val backstoryAnswers = api.fetchBackstoryAnswer(characterBackstory?.get(1)!!)
                val backstoryQuestion = api.fetchBackstoryQuestions(backstoryAnswers!!.question)
                displayBackstory(backstoryAnswers.description, backstoryQuestion!!.description)
            }

        }

        binding.profileQuestion3.setOnClickListener {
            lifecycleScope.launch {

                val characterBackstory = GlobalState.characterDetail?.backstory

                val backstoryAnswers = api.fetchBackstoryAnswer(characterBackstory?.get(2)!!)
                val backstoryQuestion = api.fetchBackstoryQuestions(backstoryAnswers!!.question)
                displayBackstory(backstoryAnswers.description, backstoryQuestion!!.description)
            }
        }

        binding.profileAnswer3.setOnClickListener {
            lifecycleScope.launch {

                val characterBackstory = GlobalState.characterDetail?.backstory

                val backstoryAnswers = api.fetchBackstoryAnswer(characterBackstory?.get(2)!!)
                val backstoryQuestion = api.fetchBackstoryQuestions(backstoryAnswers!!.question)
                displayBackstory(backstoryAnswers.description, backstoryQuestion!!.description)
            }

        }

        binding.profileQuestion4.setOnClickListener {
            lifecycleScope.launch {

                val characterBackstory = GlobalState.characterDetail?.backstory

                val backstoryAnswers = api.fetchBackstoryAnswer(characterBackstory?.get(3)!!)
                val backstoryQuestion = api.fetchBackstoryQuestions(backstoryAnswers!!.question)
                displayBackstory(backstoryAnswers.description, backstoryQuestion!!.description)
            }
        }

        binding.profileAnswer4.setOnClickListener {
            lifecycleScope.launch {

                val characterBackstory = GlobalState.characterDetail?.backstory

                val backstoryAnswers = api.fetchBackstoryAnswer(characterBackstory?.get(3)!!)
                val backstoryQuestion = api.fetchBackstoryQuestions(backstoryAnswers!!.question)
                displayBackstory(backstoryAnswers.description, backstoryQuestion!!.description)
            }

        }

        binding.profileQuestion5.setOnClickListener {
            lifecycleScope.launch {

                val characterBackstory = GlobalState.characterDetail?.backstory

                val backstoryAnswers = api.fetchBackstoryAnswer(characterBackstory?.get(4)!!)
                val backstoryQuestion = api.fetchBackstoryQuestions(backstoryAnswers!!.question)
                displayBackstory(backstoryAnswers.description, backstoryQuestion!!.description)
            }
        }

        binding.profileAnswer5.setOnClickListener {
            lifecycleScope.launch {

                val characterBackstory = GlobalState.characterDetail?.backstory

                val backstoryAnswers = api.fetchBackstoryAnswer(characterBackstory?.get(4)!!)
                val backstoryQuestion = api.fetchBackstoryQuestions(backstoryAnswers!!.question)
                displayBackstory(backstoryAnswers.description, backstoryQuestion!!.description)
            }

        }

        displayCharacterDetails()

        displayAttributes()

        return binding.root
    }



    @SuppressLint("MissingInflatedId")
    private fun displayBackstory(answer: String, question: String) {


        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_backstory, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val dialog = dialogBuilder.create()

        val backstoryQuestion = dialogView.findViewById<TextView>(R.id.profile_question_1)
        val backstoryAnswer = dialogView.findViewById<TextView>(R.id.profile_answer_1)
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