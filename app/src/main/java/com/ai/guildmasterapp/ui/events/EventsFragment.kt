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
import com.ai.guildmasterapp.LoaderDialogFragment
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

            val loader = LoaderDialogFragment.newInstance("Preparing Events") // Initializes Loading dialog
            loader.isCancelable = false
            loader.show(childFragmentManager,"loader") // Displays dialog

            setEvents()

            loader.dismiss() // Closes loading dialog after the data is fetched

        }

        val root: View = binding.root
        return root
    }


    private suspend fun setEvents() {

        val normalEventIDs = mutableListOf<String>(
            "03CFBEE1-FDF4-4698-9ED8-3BDCE69C716D",
            "EED8A79F-B374-4AE6-BA6F-B7B98D9D7142",
            "4A750A61-4BDA-4991-90A4-EFB4EAA5F33A",
            "AC6B96A6-4643-4386-B206-FD1B85E1D97E",
            "A71302C6-7D7D-4452-BFE5-1974EBE8E1B7",
        )

        val groupEventIDs = mutableListOf<String>(
            "22E2C32B-8F6D-46C9-8FF9-6453673416A5",
            "73EE3255-DCC4-478B-93E9-255180FE235A",
            "AC9C9DDA-7112-4DFF-BAA8-F4B98BDBC4E9",
            "3C71016F-F11B-40A6-BC1C-3D25C69B8B49",
            "C1A4B01C-39F5-458A-A921-B6214B7F488E",
        )

        val mapEventIDs = mutableListOf<String>(
            "D3A5D06C-2181-4CAD-AB02-0F3A9E7050A6",
            "1AB3A191-4A77-4AF2-9502-08C21369301A",
            "5FF755CD-DFC1-4BAD-A2FB-15F06D0126FE",
            "C4B15D77-56C1-4A01-9158-C05FD5D49E08",
            "301A79F7-A6EC-42CA-8834-CF0851203162",
        )

        val metaEventIDs = mutableListOf<String>(
            "7E93E453-A2A0-4416-8028-9B1396422127",
            "7B52917E-5520-4C2E-8BE0-EFD83B18D56F",
            "ACE43383-847C-4ADE-896D-C93945B6908E",
            "D81CA7AD-ED58-41F5-870F-DD651B2AC90E",
            "61B6308E-FACB-47E2-A131-C111C6B81A62",
        )

        val dungeonEventIDs = mutableListOf<String>(
            "FA7627C7-84EA-4EC4-A6C4-8F4F7EFA174A",
            "22C8B938-3DD5-49A8-BA5D-845105E8B6F0",
            "C2BB0830-A072-4E3A-A496-D9B80DB5D83D",
            "4E88E8C1-0BA1-45C6-BDF1-8854EB1E8A5C",
            "CDBFBDC5-CA98-418D-8180-CF714AA7D23F",
        )

        val fetchedNormalEvents = mutableListOf<EventDetails>()
        val fetchedGroupEvents = mutableListOf<EventDetails>()
        val fetchedMapEvents = mutableListOf<EventDetails>()
        val fetchedMetaEvents = mutableListOf<EventDetails>()
        val fetchedDungeonEvents = mutableListOf<EventDetails>()

        normalEventIDs.forEach { eventID ->

            val eventDetails = api.fetchEventDetails(eventID)
            val mapDetails = api.fetchMapDetails(eventDetails.map_id)
            eventDetails.map_name = mapDetails.name
            fetchedNormalEvents.add(eventDetails)
        }

        groupEventIDs.forEach { eventID ->

            val eventDetails = api.fetchEventDetails(eventID)
            val mapDetails = api.fetchMapDetails(eventDetails.map_id)
            eventDetails.map_name = mapDetails.name
            fetchedGroupEvents.add(eventDetails)
        }

        mapEventIDs.forEach { eventID ->

            val eventDetails = api.fetchEventDetails(eventID)
            val mapDetails = api.fetchMapDetails(eventDetails.map_id)
            eventDetails.map_name = mapDetails.name
            fetchedMapEvents.add(eventDetails)
        }

        metaEventIDs.forEach { eventID ->

            val eventDetails = api.fetchEventDetails(eventID)
            val mapDetails = api.fetchMapDetails(eventDetails.map_id)
            eventDetails.map_name = mapDetails.name
            fetchedMetaEvents.add(eventDetails)
        }

        dungeonEventIDs.forEach { eventID ->

            val eventDetails = api.fetchEventDetails(eventID)
            val mapDetails = api.fetchMapDetails(eventDetails.map_id)
            eventDetails.map_name = mapDetails.name
            fetchedDungeonEvents.add(eventDetails)
        }


        val normalAdapter = EventsAdapter(fetchedNormalEvents)
        binding.eventDetailNormalRecycler.adapter = normalAdapter
        binding.eventDetailNormalRecycler.layoutManager = LinearLayoutManager(requireContext())

        val groupAdapter = EventsAdapter(fetchedGroupEvents)
        binding.eventDetailGroupRecycler.adapter = groupAdapter
        binding.eventDetailGroupRecycler.layoutManager = LinearLayoutManager(requireContext())

        val mapAdapter = EventsAdapter(fetchedMapEvents)
        binding.eventDetailMapWideRecycler.adapter = mapAdapter
        binding.eventDetailMapWideRecycler.layoutManager = LinearLayoutManager(requireContext())

        val metaAdapter = EventsAdapter(fetchedMetaEvents)
        binding.eventDetailMetaRecycler.adapter = metaAdapter
        binding.eventDetailMetaRecycler.layoutManager = LinearLayoutManager(requireContext())

        val dungeonAdapter = EventsAdapter(fetchedDungeonEvents)
        binding.eventDetailDungeonRecycler.adapter = dungeonAdapter
        binding.eventDetailDungeonRecycler.layoutManager = LinearLayoutManager(requireContext())
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}