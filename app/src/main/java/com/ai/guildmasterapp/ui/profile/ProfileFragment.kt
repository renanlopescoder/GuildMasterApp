package com.ai.guildmasterapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ai.guildmasterapp.api.GuildWars2Api
import com.ai.guildmasterapp.databinding.FragmentMyProfileBinding.inflate
import com.ai.guildmasterapp.databinding.FragmentMyProfileBinding
import coil.load
import com.ai.guildmasterapp.Equipment
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.ItemType
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
            displayEquipments(GlobalState.characterDetail!!.equipment)
        }

        return binding.root
    }

    private suspend fun displayEquipments(equipment: List<Equipment>) {

        equipment.forEach {

            var item : ItemType? = null
            if(it.id != 97730) {
                item = api.fetchItemDetails(it.id)
            }

            when(it.slot) {
                "Helm" -> binding.profileEquipmentHelm.load(item?.icon)
                "Coat" -> binding.profileEquipmentCoat.load(item?.icon)
                "Boots" -> binding.profileEquipmentBoots.load(item?.icon)
                "Leggings" -> binding.profileEquipmentLeggings.load(item?.icon)
                "WeaponA1" -> binding.profileWeaponA1.load(item?.icon)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}