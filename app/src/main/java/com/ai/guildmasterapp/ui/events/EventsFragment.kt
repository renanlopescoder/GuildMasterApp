package com.ai.guildmasterapp.ui.events

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ai.guildmasterapp.EventDetails
import com.ai.guildmasterapp.LoaderDialogFragment
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

    private lateinit var sortType : String
    private lateinit var sortOrder: String

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


    private fun showSortingDialog(adapter: EventsAdapter) {

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_event_sort, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val closeButton = dialogView.findViewById<Button>(R.id.event_sort_close_button)

        val nameButton = dialogView.findViewById<Button>(R.id.event_sort_name)
        val levelButton = dialogView.findViewById<Button>(R.id.event_sort_level)
        val mapButton = dialogView.findViewById<Button>(R.id.event_sort_map)
        val ascendingButton = dialogView.findViewById<Button>(R.id.event_sort_ascending)
        val descendingButton = dialogView.findViewById<Button>(R.id.event_sort_descending)


        dialogBuilder.setPositiveButton(android.R.string.ok) { dialog, which ->
        }

        dialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, which ->
            dialog.cancel()
        }


        val dialog = dialogBuilder.create()


        closeButton.setOnClickListener {
            dialog.cancel()
        }


        dialog.show()

        nameButton.setOnClickListener {

            sortType = "name"

            dialog.dismiss()

            nameButton.visibility = View.GONE
            levelButton.visibility = View.GONE
            mapButton.visibility = View.GONE

            ascendingButton.visibility = View.VISIBLE
            descendingButton.visibility = View.VISIBLE

            dialog.show()
        }


        levelButton.setOnClickListener {

            sortType = "level"

            dialog.dismiss()

            nameButton.visibility = View.GONE
            levelButton.visibility = View.GONE
            mapButton.visibility = View.GONE

            ascendingButton.visibility = View.VISIBLE
            descendingButton.visibility = View.VISIBLE

            dialog.show()
        }


        mapButton.setOnClickListener {

            sortType = "map_name"

            dialog.dismiss()

            nameButton.visibility = View.GONE
            levelButton.visibility = View.GONE
            mapButton.visibility = View.GONE

            ascendingButton.visibility = View.VISIBLE
            descendingButton.visibility = View.VISIBLE

            dialog.show()
        }

        ascendingButton.setOnClickListener {
            sortOrder = "ascending"
            dialog.dismiss()
            adapter.sortEvents(sortType, sortOrder)
        }

        descendingButton.setOnClickListener {
            sortOrder = "descending"
            dialog.dismiss()
            adapter.sortEvents(sortType, sortOrder)
        }

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


// ====================== ==================== ====================== //
// ====================== Normal Events  ============================ //
// ====================== ==================== ====================== //

        val normalAdapter = EventsAdapter(fetchedNormalEvents, requireContext())
        binding.eventDetailNormalRecycler.adapter = normalAdapter
        binding.eventDetailNormalRecycler.layoutManager = LinearLayoutManager(requireContext())

        val normalSearchView = binding.eventNormalSearchView

        normalSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if(!query.isNullOrBlank()) {
                    normalAdapter.filterEvents(query)
                    binding.eventDetailNormalRecycler.scrollToPosition(0)
                }
                else {
                    normalAdapter.resetEvents()
                    binding.eventDetailNormalRecycler.scrollToPosition(0)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {

                return false
            }
        })

        val resetNormalButton = binding.eventResetButtonNormal

        resetNormalButton.setOnClickListener {
            normalAdapter.resetEvents()
            binding.eventDetailNormalRecycler.scrollToPosition(0)
        }

        val sortNormalButton = binding.eventSortButtonNormal

        sortNormalButton.setOnClickListener {
            showSortingDialog(normalAdapter)
            binding.eventDetailNormalRecycler.scrollToPosition(0)
        }



// ====================== ==================== ====================== //
// ====================== Group Events  ============================= //
// ====================== ==================== ====================== //

        val groupAdapter = EventsAdapter(fetchedGroupEvents, requireContext())
        binding.eventDetailGroupRecycler.adapter = groupAdapter
        binding.eventDetailGroupRecycler.layoutManager = LinearLayoutManager(requireContext())

        val groupSearchView = binding.eventGroupSearchView

        groupSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!query.isNullOrBlank()) {
                    groupAdapter.filterEvents(query)
                    binding.eventDetailGroupRecycler.scrollToPosition(0)
                }
                else {
                    groupAdapter.resetEvents()
                    binding.eventDetailGroupRecycler.scrollToPosition(0)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        val resetGroupButton = binding.eventResetButtonGroup

        resetGroupButton.setOnClickListener {
            groupAdapter.resetEvents()
            binding.eventDetailGroupRecycler.scrollToPosition(0)
        }

        val sortGroupButton = binding.eventSortButtonGroup

        sortGroupButton.setOnClickListener {
            showSortingDialog(groupAdapter)
            binding.eventDetailGroupRecycler.scrollToPosition(0)
        }


// ====================== ==================== ====================== //
// ====================== Map Wide Events  ========================== //
// ====================== ==================== ====================== //

        val mapAdapter = EventsAdapter(fetchedMapEvents, requireContext())
        binding.eventDetailMapWideRecycler.adapter = mapAdapter
        binding.eventDetailMapWideRecycler.layoutManager = LinearLayoutManager(requireContext())

        val mapSearchView = binding.eventMapWideSearchView

        mapSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!query.isNullOrBlank()) {
                    mapAdapter.filterEvents(query)
                    binding.eventDetailMapWideRecycler.scrollToPosition(0)
                }
                else {
                    mapAdapter.resetEvents()
                    binding.eventDetailMapWideRecycler.scrollToPosition(0)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        val resetMapButton = binding.eventResetButtonMapWide

        resetMapButton.setOnClickListener {
            mapAdapter.resetEvents()
            binding.eventDetailMapWideRecycler.scrollToPosition(0)
        }

        val sortMapButton = binding.eventSortButtonMapWide

        sortMapButton.setOnClickListener {
            showSortingDialog(mapAdapter)
            binding.eventDetailMapWideRecycler.scrollToPosition(0)
        }


// ====================== ==================== ====================== //
// ====================== Meta Events  ============================== //
// ====================== ==================== ====================== //

        val metaAdapter = EventsAdapter(fetchedMetaEvents, requireContext())
        binding.eventDetailMetaRecycler.adapter = metaAdapter
        binding.eventDetailMetaRecycler.layoutManager = LinearLayoutManager(requireContext())

        val metaSearchView = binding.eventMetaSearchView

        metaSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!query.isNullOrBlank()) {
                    metaAdapter.filterEvents(query)
                    binding.eventDetailMetaRecycler.scrollToPosition(0)
                }
                else {
                    metaAdapter.resetEvents()
                    binding.eventDetailMetaRecycler.scrollToPosition(0)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        val resetMetaButton = binding.eventResetButtonMeta

        resetMetaButton.setOnClickListener {
            metaAdapter.resetEvents()
            binding.eventDetailMetaRecycler.scrollToPosition(0)
        }

        val sortMetaButton = binding.eventSortButtonMeta

        sortMetaButton.setOnClickListener {
            showSortingDialog(metaAdapter)
            binding.eventDetailMetaRecycler.scrollToPosition(0)
        }


// ====================== ==================== ====================== //
// ====================== Dungeon Events  =========================== //
// ====================== ==================== ====================== //

        val dungeonAdapter = EventsAdapter(fetchedDungeonEvents, requireContext())
        binding.eventDetailDungeonRecycler.adapter = dungeonAdapter
        binding.eventDetailDungeonRecycler.layoutManager = LinearLayoutManager(requireContext())

        val dungeonSearchView = binding.eventDungeonSearchView

        dungeonSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!query.isNullOrBlank()) {
                    dungeonAdapter.filterEvents(query)
                    binding.eventDetailDungeonRecycler.scrollToPosition(0)
                }
                else {
                    dungeonAdapter.resetEvents()
                    binding.eventDetailDungeonRecycler.scrollToPosition(0)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        val resetDungeonButton = binding.eventResetButtonDungeon

        resetDungeonButton.setOnClickListener {
            dungeonAdapter.resetEvents()
            binding.eventDetailDungeonRecycler.scrollToPosition(0)
        }

        val sortDungeonButton = binding.eventSortButtonDungeon

        sortDungeonButton.setOnClickListener {
            showSortingDialog(dungeonAdapter)
            binding.eventDetailDungeonRecycler.scrollToPosition(0)
        }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}