package com.ai.guildmasterapp.ui.dashboard

import android.annotation.SuppressLint
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
import com.ai.guildmasterapp.GlobalState
import com.ai.guildmasterapp.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.ai.guildmasterapp.ProfessionStats
import com.ai.guildmasterapp.LadderStats

class PVPFragment : Fragment(R.layout.fragment_dashboard_pvp) {

    //Initialization of variables
    private lateinit var pvpWinLossGraph: CircularProgressIndicator
    private lateinit var winLoss: TextView
    private lateinit var rank: TextView
    private lateinit var rankPoints: TextView
    private lateinit var rankRollovers: TextView
    private lateinit var pvpSpinner: Spinner
    private lateinit var professionsCard: CardView
    private lateinit var professionWins: TextView
    private lateinit var professionLosses: TextView
    private lateinit var professionDesertions: TextView
    private lateinit var professionByes: TextView
    private lateinit var professionForfeits: TextView
    private lateinit var ladderCard: CardView
    private lateinit var ladderWins: TextView
    private lateinit var ladderLosses: TextView
    private lateinit var ladderDesertions: TextView
    private lateinit var ladderByes: TextView
    private lateinit var ladderForfeits: TextView
    private lateinit var professionsSpinner: Spinner
    private lateinit var professionIcon: ImageView
    private lateinit var ladderSpinner: Spinner
    private lateinit var ladderIcon: ImageView

    private var wins: Int = 0
    private var losses: Int = 0

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

        //Profession Cards TextViews
        professionWins = view.findViewById(R.id.pvp_professions_wins)
        professionLosses = view.findViewById(R.id.pvp_professions_losses)
        professionDesertions = view.findViewById(R.id.pvp_professions_desertions)
        professionByes = view.findViewById(R.id.pvp_professions_byes)
        professionForfeits = view.findViewById(R.id.pvp_professions_forfeits)

        //Ladder Card TextViews
        ladderWins = view.findViewById(R.id.pvp_ladder_wins)
        ladderLosses = view.findViewById(R.id.pvp_ladder_losses)
        ladderDesertions = view.findViewById(R.id.pvp_ladder_desertions)
        ladderByes = view.findViewById(R.id.pvp_ladder_byes)
        ladderForfeits = view.findViewById(R.id.pvp_ladder_forfeits)

        //Activate Spinner, to select professions or Ladder (Ranked/Unranked)
        startPVPSpinner()
        startProfSpinner()
        startLadderSpinner()

        // Fetch the data, and push it into the correct variables
        GlobalState.pvpStats?.let { pvpStats ->
            wins = pvpStats.aggregate.wins
            losses = pvpStats.aggregate.losses
            setWinLoss()
            setRank(pvpStats.pvp_rank)
            setRankPoints(pvpStats.pvp_rank_points)
            setRankRollovers(pvpStats.pvp_rank_rollovers)
            setWinLossGraph()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setWinLoss() {
        winLoss.text = "Win-Loss: $wins-$losses"
    }

    @SuppressLint("SetTextI18n")
    private fun setRank(usersRank: Int) {
        rank.text = "Rank: $usersRank"
    }

    @SuppressLint("SetTextI18n")
    private fun setRankPoints(usersRankPoints: Int) {
        rankPoints.text = "Rank Points: $usersRankPoints"
    }

    @SuppressLint("SetTextI18n")
    private fun setRankRollovers(usersRankRollovers: Int) {
        rankRollovers.text = "Rank Rollovers: $usersRankRollovers"
    }

    private fun setWinLossGraph() {
        val totalMatches: Int = wins + losses

        // value used for the circular progress bar
        val lossPercentage: Float = (losses.toFloat() / totalMatches) * 100

        pvpWinLossGraph.setProgressCompat(lossPercentage.toInt(), true)
    }

    @SuppressLint("SetTextI18n")
    private fun setProfessionsCard(profession: ProfessionStats) {
        professionWins.text = "Wins: ${profession.wins}"
        professionLosses.text = "Losses: ${profession.losses}"
        professionDesertions.text = "Desertions: ${profession.desertions}"
        professionByes.text = "Byes: ${profession.byes}"
        professionForfeits.text = "Forfeits: ${profession.forfeits}"
    }

    @SuppressLint("SetTextI18n")
    private fun setNullProfessionsCard() {
        professionWins.text = "Wins: 0"
        professionLosses.text = "Losses: 0"
        professionDesertions.text = "Desertions: 0"
        professionByes.text = "Byes: 0"
        professionForfeits.text = "Forfeits: 0"
    }

    @SuppressLint("SetTextI18n")
    private fun setLadderCard(ladder: LadderStats) {
        ladderWins.text = "Wins: ${ladder.wins}"
        ladderLosses.text = "Losses: ${ladder.losses}"
        ladderDesertions.text = "Desertions: ${ladder.desertions}"
        ladderByes.text = "Byes: ${ladder.byes}"
        ladderForfeits.text = "Forfeits: ${ladder.forfeits}"
    }

    @SuppressLint("SetTextI18n")
    private fun setNullLadderCard() {
        ladderWins.text = "Wins: 0"
        ladderLosses.text = "Losses: 0"
        ladderDesertions.text = "Desertions: 0"
        ladderByes.text = "Byes: 0"
        ladderForfeits.text = "Forfeits: 0"
    }

    private fun startPVPSpinner() {
        pvpSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
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

            override fun onNothingSelected(parent: AdapterView<*>?) {
                professionsCard.isVisible = false
                ladderCard.isVisible = false
            }
        }
    }

    private fun startProfSpinner() {
        val professionsAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.professions_spinner_items,
            R.layout.spinner_layout
        )
        professionsAdapter.setDropDownViewResource(R.layout.spinner_layout)
        professionsSpinner.adapter = professionsAdapter

        professionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                GlobalState.pvpStats?.professions?.let { professions ->
                    when (position) {
                        0 -> {
                            val mesmer = professions.mesmer
                            setProfessionsCard(mesmer)
                            professionsCard.setCardBackgroundColor(Color.parseColor("#CCB679D5"))
                            professionIcon.setImageResource(professionIcons[position])
                        }
                        1 -> {
                            val guardian = professions.guardian
                            setProfessionsCard(guardian)
                            professionsCard.setCardBackgroundColor(Color.parseColor("#CC72C1D9"))
                            professionIcon.setImageResource(professionIcons[position])
                        }
                        2 -> {
                            val necromancer = professions.necromancer
                            setProfessionsCard(necromancer)
                            professionsCard.setCardBackgroundColor(Color.parseColor("#CC52A76F"))
                            professionIcon.setImageResource(professionIcons[position])
                        }
                        3 -> {
                            val ranger = professions.ranger
                            setProfessionsCard(ranger)
                            professionsCard.setCardBackgroundColor(Color.parseColor("#CC8CDC82"))
                            professionIcon.setImageResource(professionIcons[position])
                        }
                        4 -> {
                            val elementalist = professions.elementalist
                            setProfessionsCard(elementalist)
                            professionsCard.setCardBackgroundColor(Color.parseColor("#CCF68A87"))
                            professionIcon.setImageResource(professionIcons[position])
                        }
                        5 -> {
                            val warrior = professions.warrior
                            setProfessionsCard(warrior)
                            professionsCard.setCardBackgroundColor(Color.parseColor("#CCFFD166"))
                            professionIcon.setImageResource(professionIcons[position])
                        }
                        6 -> {
                            val thief = professions.thief
                            setProfessionsCard(thief)
                            professionsCard.setCardBackgroundColor(Color.parseColor("#CCC08F95"))
                            professionIcon.setImageResource(professionIcons[position])
                        }
                        7 -> {
                            val engineer = professions.engineer
                            setProfessionsCard(engineer)
                            professionsCard.setCardBackgroundColor(Color.parseColor("#CCD09C59"))
                            professionIcon.setImageResource(professionIcons[position])
                        }
                    }
                } ?: setNullProfessionsCard()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                professionsCard.setCardBackgroundColor(Color.parseColor("#CCFFFFFF"))
            }
        }
    }

    private fun startLadderSpinner() {
        val ladderAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.ladder_spinner_items,
            R.layout.spinner_layout
        )
        ladderAdapter.setDropDownViewResource(R.layout.spinner_layout)
        ladderSpinner.adapter = ladderAdapter

        ladderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                GlobalState.pvpStats?.ladders?.let { ladders ->
                    when (position) {
                        0 -> {
                            val ranked = ladders.ranked
                            setLadderCard(ranked)
                            ladderCard.setCardBackgroundColor(Color.parseColor("#CCA61B1B"))
                            ladderIcon.setImageResource(ladderIcons[position])
                        }
                        1 -> {
                            val unranked = ladders.unranked
                            setLadderCard(unranked)
                            ladderCard.setCardBackgroundColor(Color.parseColor("#CC4CAF50"))
                            ladderIcon.setImageResource(ladderIcons[position])
                        }
                    }
                } ?: setNullLadderCard()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                ladderCard.setCardBackgroundColor(Color.parseColor("#CCFFFFFF"))
            }
        }
    }
}
