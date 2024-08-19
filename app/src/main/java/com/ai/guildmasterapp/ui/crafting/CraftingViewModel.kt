package com.ai.guildmasterapp.ui.crafting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CraftingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Crafting Fragment"
    }
    val text: LiveData<String> = _text
}