package com.ai.guildmasterapp.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ai.guildmasterapp.R
import com.google.android.material.progressindicator.CircularProgressIndicator

class PVPFragment : Fragment(R.layout.fragment_dashboard_pvp) {

    //Initialization of variables
    private lateinit var pvpWinLossGraph: CircularProgressIndicator
    private lateinit var winLoss: TextView
    private lateinit var rank: TextView
    private lateinit var rankPoints: TextView
    private lateinit var rankRollovers: TextView
    private lateinit var pvpSpinner: Spinner
    private lateinit var professionsCard: CardView
    private lateinit var ladderCard: CardView
    private lateinit var professionsSpinner: Spinner
    private lateinit var professionIcon: ImageView
    private lateinit var ladderSpinner: Spinner
    private lateinit var ladderIcon: ImageView

    // values are currently used for testing purposes, will update once the API is up
    private val wins = 20
    private val losses = 20

    private val totalMatches: Int
        get() = wins + losses

    // value used for the circular progress bar
    private val lossPercentage: Float = (losses.toFloat() / totalMatches) * 100

    // list of images, used for the professions icons
    private val professionIcons = listOf(
        R.drawable.mesmer_icon,
        R.drawable.guardian_icon,
        R.drawable.necromancer_icon,
        R.drawable.ranger_icon,
        R.drawable.elementalist_icon,
        R.drawable.warrior_icon,
        R.drawable.thief_icon,
        R.drawable.engineer_icon
    )

    // list of images, used for the ladderIcons
    private val ladderIcons = listOf(
        R.drawable.ranked_icon,
        R.drawable.unranked_icon
    )



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Overall PVP variables
        pvpWinLossGraph = view.findViewById(R.id.pvp_win_loss)
        winLoss = view.findViewById(R.id.wins_vs_loss)
        rank = view.findViewById(R.id.pvp_rank)
        rankPoints = view.findViewById(R.id.pvp_rank_points)
        rankRollovers = view.findViewById(R.id.pvp_rank_rollovers)
        pvpSpinner = view.findViewById(R.id.pvp_spinner)

        // Cards
        professionsCard = view.findViewById(R.id.pvp_professions)
        ladderCard = view.findViewById(R.id.pvp_ladder)

        // Spinners and Icons
        professionsSpinner = view.findViewById(R.id.pvp_professions_spinner)
        professionIcon = view.findViewById(R.id.profession_icon)
        ladderSpinner = view.findViewById(R.id.ladder_spinner)
        ladderIcon = view.findViewById(R.id.ladder_icon)

        //TESTING ONLY
        setWinLoss()
        setRank(5)
        setRankPoints(6513)
        setRankRollovers(4)

        //Activate Spinner, to select professions or Ladder (Ranked/Unranked)
        startPVPSpinner()
        startProfSpinner()
        startLadderSpinner()
        pvpWinLossGraph.setProgressCompat(lossPercentage.toInt(),true)













    }

    private fun setWinLoss(){
        winLoss.text = "Win-Loss: $wins-$losses"
    }

    private fun setRank(usersRank: Int){
        rank.text = "Rank: $usersRank"
    }

    private fun setRankPoints(usersRankPoints: Int){
        rankPoints.text = "Rank Points: $usersRankPoints"
    }

    private fun setRankRollovers(usersRankRollovers: Int){
        rankRollovers.text = "Rank Rollovers: $usersRankRollovers"
    }

    private fun startPVPSpinner(){
        pvpSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent:AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position){
                    0 -> {
                        professionsCard.isVisible = true
                        ladderCard.isVisible = false
                    }
                    1 -> {
                        professionsCard.isVisible = false
                        ladderCard.isVisible = true
                    }
                    else -> {
                        professionsCard.isVisible = false
                        ladderCard.isVisible = true
                    }
                }
            }
            override fun onNothingSelected(parent:AdapterView<*>?) {
                professionsCard.isVisible = false
                ladderCard.isVisible = false
            }
        }
    }

    private fun startProfSpinner(){
        //Creates an adapter for the spinner, lets me tie the array of choices and the custom layout.
        // The custom layout is needed to be able to customize the text
        val professionsAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.professions_spinner_items,
            R.layout.spinner_layout
        )

        //Tell the adapter what layout to use
        professionsAdapter.setDropDownViewResource(R.layout.spinner_layout)

        //Tie the adapter to the spinner
        professionsSpinner.adapter = professionsAdapter

        //Set the background color and icon based on the Profession chosen from the spinner
        professionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent:AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position){
                    0 -> {
                        professionsCard.setCardBackgroundColor(Color.parseColor("#CCB679D5"))
                        professionIcon.setImageResource(professionIcons[position])}
                    1 -> {
                        professionsCard.setCardBackgroundColor(Color.parseColor("#CC72C1D9"))
                        professionIcon.setImageResource(professionIcons[position])}
                    2 -> {
                        professionsCard.setCardBackgroundColor(Color.parseColor("#CC52A76F"))
                        professionIcon.setImageResource(professionIcons[position])}
                    3 -> {
                        professionsCard.setCardBackgroundColor(Color.parseColor("#CC8CDC82"))
                        professionIcon.setImageResource(professionIcons[position])}
                    4 -> {
                        professionsCard.setCardBackgroundColor(Color.parseColor("#CCF68A87"))
                        professionIcon.setImageResource(professionIcons[position])}
                    5 -> {
                        professionsCard.setCardBackgroundColor(Color.parseColor("#CCFFD166"))
                        professionIcon.setImageResource(professionIcons[position])}
                    6 -> {
                        professionsCard.setCardBackgroundColor(Color.parseColor("#CCC08F95"))
                        professionIcon.setImageResource(professionIcons[position])}
                    7 -> {
                        professionsCard.setCardBackgroundColor(Color.parseColor("#CCD09C59"))
                        professionIcon.setImageResource(professionIcons[position])}
                     }
                }

            //If nothing is chosen, set the background to white_clear. (shouldn't be able to happen)
            override fun onNothingSelected(p0: AdapterView<*>?) {
                professionsCard.setCardBackgroundColor(Color.parseColor("#CCFFFFFF"))
            }
        }
    }

    private fun startLadderSpinner(){

        //Creates an adapter for the spinner, lets me tie the array of choices and the custom layout.
        // The custom layout is needed to be able to customize the text
        val ladderAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.ladder_spinner_items,
            R.layout.spinner_layout
        )

        //Tell the adapter what layout to use
        ladderAdapter.setDropDownViewResource(R.layout.spinner_layout)

        //Tie the adapter to the spinner
        ladderSpinner.adapter = ladderAdapter


        //Set the background color and Icon based on the selection chosen from the spinner
        ladderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent:AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position){
                    0 -> {
                        ladderCard.setCardBackgroundColor(Color.parseColor("#CCA61B1B"))
                        ladderIcon.setImageResource(ladderIcons[position])}
                    1 -> {
                        ladderCard.setCardBackgroundColor(Color.parseColor("#CC4CAF50"))
                        ladderIcon.setImageResource(ladderIcons[position])}
                }
            }

            //If nothing is chosen, set the background to white_clear. (shouldn't be able to happen)
            override fun onNothingSelected(p0: AdapterView<*>?) {
                ladderCard.setCardBackgroundColor(Color.parseColor("#CCFFFFFF"))
            }
        }
    }
}