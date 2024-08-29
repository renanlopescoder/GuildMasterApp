package com.ai.guildmasterapp.ui.guild

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ai.guildmasterapp.api.GuildWars2Api
import com.ai.guildmasterapp.GuildInfo
import com.ai.guildmasterapp.ItemDetail


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

    // Returns a list of ints for every item ID in the game.
    suspend fun getItemIds(): List<Int> {
        var result: List<Int>? = null
        result = api.fetchItemIds()
        return result
    }

    // Returns an ItemDetail data class
    suspend fun getItemDetails(id: Int): ItemDetail {
        var result: ItemDetail? = null
        result = api.fetchItemDetails(id)
        return result!!
    }
}