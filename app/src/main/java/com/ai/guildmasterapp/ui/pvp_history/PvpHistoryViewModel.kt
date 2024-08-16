package com.ai.guildmasterapp.ui.pvp_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PvpHistoryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the PVP History Fragment"
    }
    val text: LiveData<String> = _text
}