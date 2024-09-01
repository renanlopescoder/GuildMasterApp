package com.ai.guildmasterapp.ui.guild

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.green
import androidx.recyclerview.widget.RecyclerView
import com.ai.guildmasterapp.GuildMember
import com.ai.guildmasterapp.R
import com.ai.guildmasterapp.api.GuildWars2Api

class GuildMemberAdapter(private val members: List<GuildMember>): RecyclerView.Adapter<GuildMemberAdapter.GuildMemberViewHolder>() {

    // ViewHolder subclass that holds the references to the views for each item
    class GuildMemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.guild_members_name)
        val rankTextView: TextView = itemView.findViewById(R.id.guild_members_rank)
        val joinedTextView: TextView = itemView.findViewById(R.id.guild_members_joined)
    }

    // Creates a new view holder when the recyclerView needs to display a new item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuildMemberViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.guild_member_recycler_items, parent, false)

        return GuildMemberViewHolder(itemView)
    }

    // Binds the data to the views inside the ViewHolder, called when a new item is about to be in view
    override fun onBindViewHolder(holder: GuildMemberViewHolder, position: Int) {
        val currentMember = members[position]
        holder.nameTextView.text = currentMember.name
        holder.rankTextView.text = currentMember.rank
        holder.joinedTextView.text = currentMember.joined

        if(position > 2) {
            holder.nameTextView.setTextColor(Color.GRAY)
        }
    }

    // Returns the total number of items in the recycler view.
    override fun getItemCount() = members.size

}