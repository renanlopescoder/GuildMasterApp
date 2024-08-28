package com.ai.guildmasterapp.ui.guild

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ai.guildmasterapp.EmblemLayer
import com.ai.guildmasterapp.api.GuildWars2Api
import com.ai.guildmasterapp.GuildInfo


class GuildViewModel : ViewModel() {

    // Initializes guild info data object
    private val _guildInfo = MutableLiveData<GuildInfo>()
    val guildInfo: LiveData<GuildInfo> get() = _guildInfo

    private val _emblemLayers = MutableLiveData<List<String>>()
    val emblemLayers: LiveData<List<String>> get() = _emblemLayers

    private val api = GuildWars2Api()

    fun getGuildInfo() {
        api.fetchGuildInfo {guildInfo ->
            _guildInfo.postValue(guildInfo)
        }
    }

    suspend fun getEmblemLayers(id: Int, type: String): List<String> {
        var result: List<String>? = null

        result = api.fetchEmblemLayers(id, type)
        return result
    }
}