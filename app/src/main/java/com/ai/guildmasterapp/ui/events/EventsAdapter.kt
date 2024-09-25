package com.ai.guildmasterapp.ui.events

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ai.guildmasterapp.EventDetails
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.R


class EventsAdapter(private val events: List<EventDetails>): RecyclerView.Adapter<EventsAdapter.EventsViewHolder>() {


    class EventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.event_detail_name)
        val levelTextView: TextView = itemView.findViewById(R.id.event_detail_level)
        val playerLevelTextView: TextView = itemView.findViewById(R.id.event_detail_player_level)
        val mapTextView: TextView = itemView.findViewById(R.id.event_detail_map)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_details_recycler_items, parent, false)

        return EventsViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val event = events[position]

        holder.nameTextView.text = event.name
        holder.levelTextView.text = event.level.toString()
        holder.playerLevelTextView.text = GlobalState.characterDetail?.level.toString()
        holder.mapTextView.text = event.map_name

        if(event.level > GlobalState.characterDetail?.level!!) {
            holder.nameTextView.setTextColor(Color.DKGRAY)
            holder.levelTextView.setTextColor(Color.RED)
            holder.playerLevelTextView.setTextColor(Color.RED)
            holder.mapTextView.setTextColor(Color.DKGRAY)

        }
    }

    override fun getItemCount() = events.size

}