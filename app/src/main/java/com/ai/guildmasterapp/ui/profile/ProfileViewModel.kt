package com.ai.guildmasterapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ai.guildmasterapp.Equipment
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.api.GuildWars2Api

class ProfileViewModel : ViewModel() {

    // Initializes equipment data object
    private val _equipment : MutableLiveData<List<Equipment>> = MutableLiveData(GlobalState.characterDetail?.equipment)
    val equipment: LiveData<List<Equipment>> = _equipment

    private val api = GuildWars2Api() // Initializes variable for the api


}