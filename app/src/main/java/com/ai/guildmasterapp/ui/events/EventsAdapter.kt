package com.ai.guildmasterapp.ui.events

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ai.guildmasterapp.EventDetails
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.R


class EventsAdapter(
    private var events: List<EventDetails>,
    private val context: Context
): RecyclerView.Adapter<EventsAdapter.EventsViewHolder>() {

    private var masterEvents: List<EventDetails> = events
    private lateinit var viewHolder: EventsAdapter.EventsViewHolder

    private val black = ContextCompat.getColor(context, R.color.black)
    private val darkGray = ContextCompat.getColor(context, R.color.dark_gray)
    private val darkGreen = ContextCompat.getColor(context, android.R.color.holo_green_dark)
    private val red = ContextCompat.getColor(context, R.color.red)

    class EventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.event_detail_name)
        val levelTextView: TextView = itemView.findViewById(R.id.event_detail_level)
        val playerLevelTextView: TextView = itemView.findViewById(R.id.event_detail_player_level)
        val mapTextView: TextView = itemView.findViewById(R.id.event_detail_map)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_details_recycler_items, parent, false)

        viewHolder = EventsViewHolder(itemView)

        return viewHolder
    }


    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val event = events[position]


        holder.nameTextView.text = event.name
        holder.levelTextView.text = event.level.toString()
        holder.playerLevelTextView.text = GlobalState.characterDetail?.level.toString()
        holder.mapTextView.text = event.map_name


        holder.nameTextView.setTextColor(black)
        holder.levelTextView.setTextColor(darkGreen)
        holder.playerLevelTextView.setTextColor(black)
        holder.mapTextView.setTextColor(black)



        if(event.level > GlobalState.characterDetail?.level!!) {
            holder.nameTextView.setTextColor(darkGray)
            holder.levelTextView.setTextColor(red)
            holder.playerLevelTextView.setTextColor(red)
            holder.mapTextView.setTextColor(darkGray)

        }
    }

    override fun getItemCount() = events.size


    fun updateEvents(filteredEvents: List<EventDetails>) {
        events = filteredEvents
        notifyDataSetChanged()
    }

    fun resetEvents() {
        events = masterEvents
        notifyDataSetChanged()
    }

    fun filterEvents(query: String) {

        val filteredEvents = mutableListOf<EventDetails>()

        masterEvents.forEach { event ->

            if(event.name.contains(query, ignoreCase = true)) {
                filteredEvents.add(event)
            }

            if(event.level.toString() == query) {
                filteredEvents.add(event)
            }

            if(event.map_name!!.contains(query, ignoreCase = true)) {
                filteredEvents.add(event)
            }
        }

        updateEvents(filteredEvents)
    }



    fun sortEvents(sortType: String, sortOrder: String) {

        var sortedEvents : List<EventDetails>? = null

        if(sortType == "name") {
            if (sortOrder == "ascending") {
                sortedEvents = masterEvents.sortedBy { it.name }
            } else {
                sortedEvents = masterEvents.sortedByDescending { it.name }
            }
        }

        if(sortType == "level") {
            if (sortOrder == "ascending") {
                sortedEvents = masterEvents.sortedBy { it.level }
            }
            else {
                sortedEvents = masterEvents.sortedByDescending { it.level }
            }
        }

        if(sortType == "map_name") {
            if (sortOrder == "ascending") {
                sortedEvents = masterEvents.sortedBy { it.map_name }
            }
            else {
                sortedEvents = masterEvents.sortedByDescending { it.map_name }
            }
        }

        updateEvents(sortedEvents!!)

    }


}