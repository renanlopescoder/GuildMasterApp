package com.ai.guildmasterapp.ui.guild

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GuildViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Guild Fragment"
    }
    val text: LiveData<String> = _text
}