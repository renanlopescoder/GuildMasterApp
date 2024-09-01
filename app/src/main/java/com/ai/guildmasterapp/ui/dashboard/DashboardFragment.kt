package com.ai.guildmasterapp.ui.dashboard

import android.annotation.SuppressLint
//import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
//import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ai.guildmasterapp.R
//import android.widget.PopupWindow
import android.widget.ImageButton
import com.ai.guildmasterapp.GlobalState
//import androidx.constraintlayout.widget.ConstraintLayout
import com.ai.guildmasterapp.databinding.FragmentDashboardBinding



class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    //Variables
    private lateinit var dashboardGreeting: TextView
    private lateinit var profileImage: ImageView
    private lateinit var overviewToggleButton: ToggleButton
    private lateinit var pvpToggleButton: ToggleButton
    private lateinit var filterButton: ImageButton

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Assign the variables to their respective IDs
        dashboardGreeting = root.findViewById(R.id.dashboard_greeting)
        profileImage = root.findViewById(R.id.dashboard_temp_prof_pic)
        overviewToggleButton = root.findViewById(R.id.dashboard_overview_toggle)
        pvpToggleButton = root.findViewById(R.id.dashboard_pvp_toggle)
        filterButton = root.findViewById(R.id.filter_button)

        setDashboardGreeting()


        //Initialize Dashboard with Overview Fragment
        childFragmentManager.beginTransaction()
            .replace(R.id.dashboard_fragment_container, OverviewFragment())
            .commit()

        //Change the fragment based on which toggle is clicked, and set the opposite toggle to off
        overviewToggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.dashboard_fragment_container, OverviewFragment())
                    .commit()
                pvpToggleButton.isChecked = false
            }
        }

        pvpToggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.dashboard_fragment_container, PVPFragment())
                    .commit()
                overviewToggleButton.isChecked = false
            }
        }



//        //This brings up the popup window
//        filterButton.setOnClickListener{
//            showFilters(it)
//        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setDashboardGreeting() {
        val userProfession = GlobalState.characterDetail?.profession
        if (userProfession != null) {
            "Hello, \n$userProfession".also { dashboardGreeting.text = it }
        }
    }

    private fun setProfileImage(usersProfilePic: Int) {
        profileImage.setImageResource(usersProfilePic)
    }
}


