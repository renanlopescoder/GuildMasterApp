package com.ai.guildmasterapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ai.guildmasterapp.api.GuildWars2Api
import com.ai.guildmasterapp.databinding.FragmentMyProfileBinding
import coil.load
import com.ai.guildmasterapp.Equipment
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.ItemType
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

        lifecycleScope.launch {

            displayBackstory()
        }

        displayCharacterDetails()

        displayAttributes()

        return binding.root
    }



    private suspend fun displayBackstory() {

        val characterBackstory = GlobalState.characterDetail?.backstory


        val backstory = api.fetchBackstoryAnswer(characterBackstory?.get(0)!!)

        binding.profileAnswer1.text = backstory?.description

        /*characterBackstory?.forEach {answer ->
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