package com.ai.guildmasterapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ai.guildmasterapp.GlobalState

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Character: " + GlobalState.characterDetail?.name + " Player PVP Rank: " + GlobalState.pvpStats?.pvp_rank
    }
    val text: LiveData<String> = _text
}