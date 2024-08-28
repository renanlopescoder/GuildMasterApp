package com.ai.guildmasterapp.ui.guild

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ai.guildmasterapp.api.GuildWars2Api
import com.ai.guildmasterapp.GuildInfo


class GuildViewModel : ViewModel() {

    // Initializes guild info data object
    private val _guildInfo = MutableLiveData<GuildInfo>()
    val guildInfo: LiveData<GuildInfo> get() = _guildInfo

    private val api = GuildWars2Api() // Initializes variable for the api

    // Fetches Guild information
    fun getGuildInfo() {
        api.fetchGuildInfo {guildInfo ->
            _guildInfo.postValue(guildInfo)
        }
    }

    // Returns Layers of URLs to render the image through Coli
    suspend fun getEmblemLayers(id: Int, type: String): List<String> {
        var result: List<String>? = null

        result = api.fetchEmblemLayers(id, type)
        return result
    }
}