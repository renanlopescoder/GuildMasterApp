package com.ai.guildmasterapp.ui.hamburgerMenu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HamburgerMenuViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Hamburger Menu Fragment"
    }
    val text: LiveData<String> = _text
}