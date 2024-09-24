package com.ai.guildmasterapp.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ai.guildmasterapp.EventDetails
import com.ai.guildmasterapp.R
import com.ai.guildmasterapp.api.GuildWars2Api
import com.ai.guildmasterapp.databinding.FragmentEventsBinding
import kotlinx.coroutines.launch

class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val api = GuildWars2Api()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(EventsViewModel::class.java)

        _binding = FragmentEventsBinding.inflate(inflater, container, false)

        lifecycleScope.launch {

            val eventIDs = mutableListOf<String>(
                "03CFBEE1-FDF4-4698-9ED8-3BDCE69C716D",
                "EED8A79F-B374-4AE6-BA6F-B7B98D9D7142",
                "4A750A61-4BDA-4991-90A4-EFB4EAA5F33A",
                "AC6B96A6-4643-4386-B206-FD1B85E1D97E",
                "A71302C6-7D7D-4452-BFE5-1974EBE8E1B7",
            )

            val fetchedEvents = mutableListOf<EventDetails>()

            eventIDs.forEach { eventID ->

                val eventDetails = api.fetchEventDetails(eventID)
                val mapDetails = api.fetchMapDetails(eventDetails.map_id)
                eventDetails.map_name = mapDetails.name
                fetchedEvents.add(eventDetails)
            }

            val adapter = EventsAdapter(fetchedEvents)
            binding.eventDetailRecycler.adapter = adapter
            binding.eventDetailRecycler.layoutManager = LinearLayoutManager(requireContext())

        }

        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}